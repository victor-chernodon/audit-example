locals {
  # We don't create region specific roles and policies.
  # This will ensure that it will only be created once in every namespace.
  attach_aws_managed_roles = [
    "arn:aws:iam::488021763009:policy/config-retriever",
  ]
  create_role_and_policy = var.region == "eu-central-1" ? 1 : 0
}

module "dynamodb" {
  source      = "./modules/dynamo-db"
  tags        = local.common_tags
  region      = var.region
  environment = var.environment
  service     = var.service
  teams       = var.teams

  autoscaling     = true
  replication     = var.environment == "prod" ? true : false
  prefix_override = "audit" # otherwise it would be audit-api
  name            = "events"
  hash_key        = "customer#suffix"
  range_key       = "timestampUTC"
  attributes = [
    {
      name = "customer#suffix"
      type = "S"
    },
    {
      name = "timestampUTC"
      type = "S"
    },
  ]
}

resource "aws_iam_role" "role" {
  count              = local.create_role_and_policy
  name               = "k8s_audit_${var.environment}"
  assume_role_policy = file("policies/audit-api_role.json")
}

resource "aws_iam_role_policy" "audit-api" {
  count  = local.create_role_and_policy
  name   = "audit-api_${var.environment}"
  role   = aws_iam_role.role[0].id
  policy = templatefile("policies/audit-api_policy.json", { "env" : var.environment })
}

resource "aws_iam_role_policy_attachment" "aws_managed_roles" {
  for_each   = local.create_role_and_policy == 1 ? toset(local.attach_aws_managed_roles) : []
  role       = aws_iam_role.role[0].id
  policy_arn = each.key
}