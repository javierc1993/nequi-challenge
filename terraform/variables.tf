# terraform/variables.tf

variable "aws_region" {
  description = "Región de AWS para el despliegue."
  type        = string
  default     = "us-east-2"
}

variable "db_name" {
  description = "Nombre de la base de datos en RDS."
  type        = string
  default     = "franchisedb"
}

variable "db_user" {
  description = "Usuario para la base de datos RDS."
  type        = string
  default     = "postgresadmin"
}

variable "db_password" {
  description = "Contraseña para la base de datos RDS."
  type        = string
  sensitive   = true
}

variable "ecr_image_uri" {
  description = "La URL completa de la imagen Docker en ECR (incluyendo el tag :latest)."
  type        = string
}