# terraform/outputs.tf

output "rds_database_endpoint" {
  description = "El endpoint de la base de datos RDS."
  value       = aws_db_instance.franchise_db.endpoint
}

output "instructions" {
  description = "Instrucciones para encontrar la IP pública de tu API."
  value       = "Ve a la consola de AWS -> ECS -> Clusters -> franchise-cluster -> franchise-api-service -> Tasks tab. Haz clic en la tarea y busca la 'Public IP'."
}