package com.imanage.aws.wrapper;

import com.amazonaws.services.ec2.model.*;

/**
 * This will eventually be used for spinning up an EC2 instance that we can grab keys from that are
 * properly scoped and secure.
 */
public class EC2Service {
    public RunInstancesRequest getInstance() {
        RunInstancesRequest request =  new RunInstancesRequest();

        request.withImageId("ami-a9d09ed1") // Amazon Machine Image ID
                .withInstanceType(InstanceType.T1Micro) // instance type compatible with the AMI
                .withMinCount(1).withMaxCount(1)
                .withKeyName("my-key-pair")  // ec2 key pair name
                .withSecurityGroups("my-security-group");  // ec2 security group name

        return request;
    }
}
