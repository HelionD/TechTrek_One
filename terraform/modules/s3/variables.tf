variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "techtrek"
}

variable "environment" {
  description = "Environment name (local, prod)"
  type        = string
  default     = "local"
}

variable "bucket_suffix" {
  description = "Suffix for the bucket name to make it unique (e.g. images, logs, backups)"
  type        = string
  default     = "images"
}

variable "versioning_enabled" {
  description = "Enable versioning on the bucket"
  type        = bool
  default     = true
}
