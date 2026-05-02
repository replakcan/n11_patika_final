variable "aws_region" {
  description = "AWS region for the EC2 instance."
  type        = string
  default     = "eu-central-1"
}

variable "project_name" {
  description = "Name prefix for AWS resources."
  type        = string
  default     = "n11-patika"
}

variable "environment" {
  description = "Environment name used in tags."
  type        = string
  default     = "dev"
}

variable "instance_type" {
  description = "EC2 instance type. t3.small is the low-cost Spot default for this demo stack; use t3.medium if memory pressure persists."
  type        = string
  default     = "t3.small"
}

variable "key_name" {
  description = "Optional EC2 key pair name for SSH access."
  type        = string
  default     = null
}

variable "ssh_cidr_blocks" {
  description = "CIDR blocks allowed to SSH to the instance."
  type        = list(string)
  default     = []
}

variable "app_cidr_blocks" {
  description = "CIDR blocks allowed to reach the app ports."
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "repository_url" {
  description = "Git repository URL cloned by EC2 user data."
  type        = string
}

variable "git_ref" {
  description = "Branch, tag, or commit to deploy on first boot."
  type        = string
  default     = "main"
}

variable "app_dir" {
  description = "Directory where the app repository is cloned on EC2."
  type        = string
  default     = "/opt/n11-patika"
}

variable "image_registry" {
  description = "Docker image namespace used by Compose, for example a Docker Hub username."
  type        = string
  default     = "replakcan"
}

variable "image_tag" {
  description = "Docker image tag to run."
  type        = string
  default     = "latest"
}

variable "postgres_username" {
  description = "Postgres username."
  type        = string
  default     = "postgres"
}

variable "postgres_password" {
  description = "Postgres password."
  type        = string
  sensitive   = true
}

variable "rabbitmq_username" {
  description = "RabbitMQ username."
  type        = string
  default     = "guest"
}

variable "rabbitmq_password" {
  description = "RabbitMQ password."
  type        = string
  sensitive   = true
}

variable "keycloak_admin" {
  description = "Keycloak admin username."
  type        = string
  default     = "admin"
}

variable "keycloak_admin_password" {
  description = "Keycloak admin password."
  type        = string
  sensitive   = true
}

variable "keycloak_client_secret" {
  description = "Keycloak frontend client secret."
  type        = string
  sensitive   = true
}

variable "nextauth_secret" {
  description = "NextAuth secret."
  type        = string
  sensitive   = true
}

variable "iyzico_api_key" {
  description = "Iyzico API key."
  type        = string
  sensitive   = true
  default     = ""
}

variable "iyzico_secret_key" {
  description = "Iyzico secret key."
  type        = string
  sensitive   = true
  default     = ""
}

variable "iyzico_callback_url" {
  description = "Public payment callback URL. Leave empty to use http://EC2_PUBLIC_IP:8079/api/payments/callback."
  type        = string
  default     = ""
}

variable "payment_callback_origin_url" {
  description = "HTTP origin URL that the HTTPS payment callback API Gateway proxies to."
  type        = string
  default     = ""
}

variable "install_ngrok" {
  description = "Install and start an optional ngrok tunnel for api-gateway port 8079. Prefer a real domain/ALB for anything beyond testing."
  type        = bool
  default     = false
}

variable "ngrok_authtoken" {
  description = "Ngrok authtoken, required when install_ngrok is true."
  type        = string
  sensitive   = true
  default     = ""
}

variable "ngrok_domain" {
  description = "Optional static ngrok domain. Useful because random ngrok URLs cannot be known before docker compose starts."
  type        = string
  default     = ""
}
