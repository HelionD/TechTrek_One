resource "aws_rds_cluster" "this" {
  cluster_identifier     = var.name
  engine                 = "aurora-postgresql"
  database_name         = var.db_name
  master_username       = var.username
  master_password       = var.password
  skip_final_snapshot   = true
  vpc_security_group_ids = var.security_group_ids
}