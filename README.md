# aws-key-wrapper
Research spike investigating application of AWS' Key Management Service (KMS)

# Usage

1. Navigate to `src/main/java/com/imanage/aws/wrapper`.  
2. Fill out the `dev.env` with AWS credentials.  
3. Run the `Application` file.

## Obtaining AWS Credentials

1. Log onto the AWS Console 
2. Go to the IAM (Identity Access Management)
3. [optional] Create a Role that allows EC2 Access to AWS. 
4. Create a new User and grab the AWS_ACCESS_KEY and AWS_SECRET_ACCESS_KEY from them. 
5. Go to KMS (Key Management Service)
6. Create a new key for encryption only and give the user created in step 4 access to it.

