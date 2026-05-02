# Infrastructure

This Terraform stack creates a simple single-EC2 deployment for local/cloud smoke testing:

- Docker Hub images for the application services
- One Amazon Linux 2023 EC2 Spot instance
- An IAM role for SSM access
- A security group exposing frontend `3000`, api-gateway `8079`, and Keycloak `8085`
- User data that installs Docker, enables Docker, clones this repo, writes `.env.compose.local`, and runs Docker Compose

## Usage

Create the S3 bucket for Terraform state once before `terraform init`. The bucket
name must be globally unique:

```powershell
aws s3api create-bucket --bucket YOUR_UNIQUE_STATE_BUCKET --region eu-central-1 --create-bucket-configuration LocationConstraint=eu-central-1
aws s3api put-bucket-versioning --bucket YOUR_UNIQUE_STATE_BUCKET --versioning-configuration Status=Enabled
aws s3api put-bucket-encryption --bucket YOUR_UNIQUE_STATE_BUCKET --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
```

```powershell
cd infrastructure
copy terraform.tfvars.example terraform.tfvars
copy backend.hcl.example backend.hcl
# Edit terraform.tfvars and backend.hcl before init/apply.
terraform init -backend-config=backend.hcl
terraform apply
```

`backend.tf` contains safe placeholder values so the module remains easy to
validate, while your real bucket name should live in ignored `backend.hcl`.
`backend.hcl`, `terraform.tfvars`, and local state files are ignored by git. Do
not commit them because they contain account-specific configuration and can
expose sensitive values through Terraform state.

After `terraform apply`, Terraform will print the EC2 URLs and image settings.
The first boot will try to pull application images from Docker Hub using:

```text
IMAGE_REGISTRY=replakcan
IMAGE_TAG=latest
```

If those images are not in Docker Hub yet, the instance will still be created and left
ready with Docker, Docker Compose, AWS CLI, the cloned repository, and
`.env.compose.local`.

You have two practical options:

- Build and push images to Docker Hub manually from your machine.
- SSH/SSM into the instance and run `docker compose --env-file .env.compose.local up -d --build` from `/opt/n11-patika`.

Building on the EC2 instance is simpler, but it is much heavier than pulling
prebuilt images.

## Free Tier Note

`t3.micro`/`t2.micro` can be free-tier eligible, but this app is heavy for one micro instance because it runs Postgres, RabbitMQ, Keycloak, seven Spring services, and Next.js. The default is `t3.small` Spot with standard CPU credits for a low-cost demo. If containers still restart or get OOM-killed, use `t3.medium`.

Spot instances can be interrupted and the local Postgres volume lives on the instance root disk. Treat this stack as disposable demo infrastructure unless you add backups or move stateful services out of the instance.

## Ngrok

Ngrok is optional and disabled by default. For a cloud VM, direct public IP or a real HTTPS domain is simpler. Use ngrok only for temporary payment callback testing, ideally with a reserved ngrok domain so the callback URL is stable before Compose starts.
