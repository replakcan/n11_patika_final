locals {
  tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_iam_role" "ec2" {
  name = "${var.project_name}-${var.environment}-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = local.tags
}

resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.ec2.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2" {
  name = "${var.project_name}-${var.environment}-ec2-profile"
  role = aws_iam_role.ec2.name
}

resource "aws_security_group" "app" {
  name        = "${var.project_name}-${var.environment}-sg"
  description = "Access for ${var.project_name}"

  dynamic "ingress" {
    for_each = length(var.ssh_cidr_blocks) > 0 ? [1] : []

    content {
      description = "SSH"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = var.ssh_cidr_blocks
    }
  }

  ingress {
    description = "Frontend"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = var.app_cidr_blocks
  }

  ingress {
    description = "API gateway"
    from_port   = 8079
    to_port     = 8079
    protocol    = "tcp"
    cidr_blocks = var.app_cidr_blocks
  }

  ingress {
    description = "Keycloak"
    from_port   = 8085
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = var.app_cidr_blocks
  }

  egress {
    description = "Outbound internet"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, {
    Name = "${var.project_name}-${var.environment}-sg"
  })
}

resource "aws_instance" "app" {
  ami                         = data.aws_ami.amazon_linux_2023.id
  instance_type               = var.instance_type
  key_name                    = var.key_name
  iam_instance_profile        = aws_iam_instance_profile.ec2.name
  vpc_security_group_ids      = [aws_security_group.app.id]
  associate_public_ip_address = true

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  credit_specification {
    cpu_credits = "standard"
  }

  instance_market_options {
    market_type = "spot"

    spot_options {
      instance_interruption_behavior = "terminate"
      spot_instance_type             = "one-time"
    }
  }

  user_data_replace_on_change = true
  user_data = templatefile("${path.module}/user_data.sh.tftpl", {
    aws_region              = var.aws_region
    repository_url          = var.repository_url
    git_ref                 = var.git_ref
    app_dir                 = var.app_dir
    image_registry          = var.image_registry
    image_tag               = var.image_tag
    postgres_username       = var.postgres_username
    postgres_password       = var.postgres_password
    rabbitmq_username       = var.rabbitmq_username
    rabbitmq_password       = var.rabbitmq_password
    keycloak_admin          = var.keycloak_admin
    keycloak_admin_password = var.keycloak_admin_password
    keycloak_client_secret  = var.keycloak_client_secret
    nextauth_secret         = var.nextauth_secret
    iyzico_api_key          = var.iyzico_api_key
    iyzico_secret_key       = var.iyzico_secret_key
    iyzico_callback_url     = var.iyzico_callback_url
    install_ngrok           = var.install_ngrok
    ngrok_authtoken         = var.ngrok_authtoken
    ngrok_domain            = var.ngrok_domain
  })

  tags = merge(local.tags, {
    Name = "${var.project_name}-${var.environment}"
  })
}

resource "aws_apigatewayv2_api" "payment_callback" {
  name          = "${var.project_name}-${var.environment}-payment-callback"
  protocol_type = "HTTP"

  tags = local.tags
}

resource "aws_apigatewayv2_integration" "payment_callback" {
  api_id                 = aws_apigatewayv2_api.payment_callback.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = var.payment_callback_origin_url
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "payment_callback" {
  api_id    = aws_apigatewayv2_api.payment_callback.id
  route_key = "ANY /api/payments/callback"
  target    = "integrations/${aws_apigatewayv2_integration.payment_callback.id}"
}

resource "aws_apigatewayv2_stage" "payment_callback" {
  api_id      = aws_apigatewayv2_api.payment_callback.id
  name        = "$default"
  auto_deploy = true

  tags = local.tags
}
