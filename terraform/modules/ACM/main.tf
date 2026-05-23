resource "aws_acm_certificate" "this" {
  domain_name       = var.domain
  validation_method = "DNS"
}