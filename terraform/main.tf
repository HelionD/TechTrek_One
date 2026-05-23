module "vpc" {
  source = "./modules/vpc"
}

module "s3" {
  source        = "./modules/s3"
  bucket_suffix = "demo"
}

module "ec2" {
  source = "./modules/ec2"
}