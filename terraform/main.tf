# main.tf

provider "aws" {
  region = var.aws_region
}

# --- 1. BUSCAR LA RED POR DEFECTO (en lugar de crearla) ---
# Le decimos a Terraform que encuentre la VPC por defecto en la región seleccionada.
data "aws_vpc" "default" {
  default = true
}

# Le decimos a Terraform que encuentre TODAS las subredes dentro de esa VPC por defecto.
# AWS crea subredes públicas por defecto en cada zona de disponibilidad.
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}


# --- 2. SEGURIDAD (Grupos de Seguridad / Firewall) ---
# El grupo de seguridad para Fargate ahora se asocia a la VPC por defecto.
resource "aws_security_group" "fargate_sg" {
  name   = "franchise-fargate-sg"
  vpc_id = data.aws_vpc.default.id # <-- CAMBIO

  ingress {
    protocol    = "tcp"
    from_port   = 8080
    to_port     = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# El grupo de seguridad para RDS también se asocia a la VPC por defecto.
resource "aws_security_group" "rds_sg" {
  name   = "franchise-rds-sg"
  vpc_id = data.aws_vpc.default.id # <-- CAMBIO

  ingress {
    protocol        = "tcp"
    from_port       = 5432
    to_port         = 5432
    security_groups = [aws_security_group.fargate_sg.id]
  }
}


# --- 3. BASE DE DATOS (RDS PostgreSQL) ---
# El grupo de subredes para RDS ahora usa las subredes por defecto que encontramos.
resource "aws_db_subnet_group" "default" {
  name       = "franchise-db-subnet-group"
  subnet_ids = data.aws_subnets.default.ids # <-- CAMBIO
}

resource "aws_db_instance" "franchise_db" {
  identifier           = "franchise-api-db"
  engine               = "postgres"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20

  db_name              = var.db_name
  username             = var.db_user
  password             = var.db_password

  db_subnet_group_name = aws_db_subnet_group.default.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  skip_final_snapshot  = true
  # Ya no necesitamos 'publicly_accessible' porque las subredes por defecto ya lo son.
}


# --- 4. APLICACIÓN (ECS Fargate) ---
# (Esta sección no necesita cambios en su lógica interna)
resource "aws_ecs_cluster" "main" {
  name = "franchise-cluster"
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "franchise-ecs-task-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_task_definition" "api_task" {
  family                   = "franchise-api-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  container_definitions = jsonencode([{
    name  = "franchise-api-container",
    image = var.ecr_image_uri,
    portMappings = [{ containerPort = 8080, hostPort = 8080 }],
    environment = [
      { name = "SPRING_R2DBC_URL", value = "r2dbc:postgresql://${aws_db_instance.franchise_db.endpoint}/${var.db_name}" },
      { name = "SPRING_R2DBC_USERNAME", value = var.db_user },
      { name = "SPRING_R2DBC_PASSWORD", value = var.db_password }
    ],
    logConfiguration = {
      logDriver = "awslogs",
      options = {
        "awslogs-group"         = "/ecs/franchise-api",
        "awslogs-region"        = var.aws_region,
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}

resource "aws_cloudwatch_log_group" "ecs_logs" {
  name = "/ecs/franchise-api"
}

resource "aws_ecs_service" "main" {
  name            = "franchise-api-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.api_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.default.ids # <-- CAMBIO
    security_groups  = [aws_security_group.fargate_sg.id]
    assign_public_ip = true
  }
}

# (El ECR Repository no está en el main.tf, pero asegúrate de que exista o añádelo si es necesario)
resource "aws_ecr_repository" "api_repo" {
  name = "nequi-challenge"
  image_tag_mutability = "MUTABLE"
  force_delete         = true
}