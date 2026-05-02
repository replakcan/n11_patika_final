output "instance_id" {
  description = "EC2 instance ID."
  value       = aws_instance.app.id
}

output "public_ip" {
  description = "EC2 public IP."
  value       = aws_instance.app.public_ip
}

output "public_dns" {
  description = "EC2 public DNS name."
  value       = aws_instance.app.public_dns
}

output "frontend_url" {
  description = "Frontend URL."
  value       = "http://${aws_instance.app.public_dns}:3000"
}

output "api_gateway_url" {
  description = "API gateway URL."
  value       = "http://${aws_instance.app.public_dns}:8079"
}

output "payment_callback_https_url" {
  description = "HTTPS payment callback URL for Iyzico through API Gateway."
  value       = "${aws_apigatewayv2_api.payment_callback.api_endpoint}/api/payments/callback"
}

output "keycloak_url" {
  description = "Keycloak URL."
  value       = "http://${aws_instance.app.public_dns}:8085"
}

output "image_registry" {
  description = "Docker image namespace used by Compose."
  value       = var.image_registry
}
