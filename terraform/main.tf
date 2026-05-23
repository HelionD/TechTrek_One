module "vpc" {
  source = "./modules/vpc"
}

module "s3" {
  source      = "./modules/s3"
  bucket_name = "my-app-bucket"
}

module "ec2" {
  source = "./modules/ec2"
}

module "ecr_backend" {
  source = "./modules/ecr"
  name   = "backend"
}

module "ecr_frontend" {
  source = "./modules/ecr"
  name   = "frontend"
}

module "eks" {
  source       = "./modules/eks"
  cluster_name = "main-cluster"
  vpc_id       = module.vpc.vpc_id
  subnet_ids   = module.vpc.private_subnets
}

module "aurora" {
  source             = "./modules/aurora"
  name               = "app-db"
  username           = "admin"
  password           = "password123"
  security_group_ids = []
}

module "route53" {
  source = "./modules/route53"
  domain = "one.al"
}

module "acm" {
  source = "./modules/acm"
  domain = "one.al"
}

module "alb" {
  source  = "./modules/alb"
  name    = "app-alb"
  subnets = module.vpc.public_subnets
}