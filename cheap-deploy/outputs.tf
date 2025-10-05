output "application_url" {
  description = "EnergyFlow Application URL"
  value       = "http://${aws_instance.backend.public_ip}"
}

output "api_url" {
  description = "Backend API URL"
  value       = "http://${aws_instance.backend.public_ip}/api"
}

output "swagger_url" {
  description = "Swagger documentation URL"
  value       = "http://${aws_instance.backend.public_ip}/api/swagger-ui.html"
}

output "ssh_command" {
  description = "SSH command to connect to server"
  value       = "ssh -i ~/.ssh/id_rsa ec2-user@${aws_instance.backend.public_ip}"
}

output "timescaledb_info" {
  description = "TimescaleDB connection info"
  value       = "Host: ${aws_instance.backend.public_ip}:5432, DB: weatherdb, User: weather_user"
}