variable "name" {}
variable "db_name" {
  default = "appdb"
}
variable "username" {}
variable "password" {}
variable "security_group_ids" {
  type = list(string)
}