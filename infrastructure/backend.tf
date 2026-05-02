terraform {
  backend "s3" {
    bucket       = "replace-this-with-your-terraform-state-bucket"
    key          = "n11-patika/dev/terraform.tfstate"
    region       = "eu-central-1"
    encrypt      = true
    use_lockfile = true
  }
}
