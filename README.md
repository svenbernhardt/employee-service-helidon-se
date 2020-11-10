# Cloud-native API-driven services on OCI

## Prerequisite

You'll need to have the following OCI service up and running

* OCI API Gateway: Read more about setting up a new instance in the official [documentation](https://docs.cloud.oracle.com/en-us/iaas/Content/APIGateway/Tasks/apigatewaycreatinggateway.htm)
* Oracle Container Engine for Kubernetes (OKE): Read more about setting up a new instance in the official  [documentation](https://docs.cloud.oracle.com/en-us/iaas/Content/ContEng/Concepts/contengoverview.htm)
* Oracle Cloud Infrastructure Registry (OCIR): Available OOTB

*Note:* As an alternative to OKE, you could also simply use a Compute Node instance to run the service using the startup command

```java -jar employee-service-se.jar```

## Build and test

The following steps describe how to build and test the service properly. 

1. Build the project using Maven

    Building the application is done be using Maven. To execute the Maven  build, use the following command.

    ``` 
    mvn clean package -DskipTests
    ```
   
    *Note:* JUnit tests are currently failing, so tests need to be skipped!

2. Test the API contract against the implementation using Dredd

    To test, if the implementation adheres to the API contract, execute test using Dredd. Dredd is an NPM based 
    tool that can simply be installed following the [documentation](https://dredd.org/en/latest/installation.html).
    
    To execute dredd, just execute the following command.

    ``` 
    dredd
    ```
    
    *Note:* Currently only the happy path tests are working. Exceptional paths are failing.

## Manual deployment to Oracle Container Engine for Kubernetes

1. Build the docker image

    Build and tag the Docker image from the command line.
    
    ``` 
    docker build . -t <region-key>.ocir.io/<tenancy-namespace>/employee-service:0.1.0
    
    Example:
      region-key for Frankfurt -> fra
    ```

2. Login to OCIR and push Docker image

    Next, you need to login to OCIR available within your tenancy. Doing so requires to you have registered a
    respective Auth Token to the IAM User, you're using to interact with OCIR.
    
    ``` 
    docker login <region-key>.ocir.io
    
    docker push <region-key>.ocir.io/<tenancy-namespace>/employee-service:0.1.0
    ```

3. Get kubeconfig file for your Cluster

    To being able to communicate with your K8s cluster, you need to create a respective kubeconfig on your local 
    system or you simply use OCI Cloud Shell (where you also need to execute the command below)
    
    ```
    oci ce cluster create-kubeconfig --cluster-id <oke-cluster-ocid> --file $HOME/.kube/config --region <oci-region> --token-version 2.0.0 
    ``` 

4. Deploy Ingress Controller

    As an ingress controller, I choose Kong Ingress Controller. To install the Ingress Controller to the running OKE
    Cluster, simply make use of the respective Helm charts.
    
    ```
    kubectl create ns ingress
    
    kubectl config set-context --current --namespace=ingress
    
    helm repo add kong https://charts.konghq.com
    helm repo update
    helm install kong/kong --generate-name --set ingressController.installCRDs=false
    ```
    
    *Note:* More information with respect to Kong Ingress Controller can be found [here](https://github.com/Kong/kubernetes-ingress-controller)

5. Deploy Helidon SE Service

    The deployment of the Helidon SE service to OKE is done using kubectl CLI
    
    ```
    kubectl create ns employees
    
    kubectl config set-context --current --namespace=employees
    
    kubectl apply -f app.yaml
    
    kubectl apply -f ingress.yaml
    ```
 
 After step 5, you should have a valid and reachable instance of the Helidon SE service, which is exposed
 by the Ingress Controller.
 
 ## Create and deploy API in OCI API Gateway
 
 In this version of the document, you need to manually add the API deployment using OCI Console. This can be
 done by using the Open API description of the service from this repository.
 
 You may add any policies you like and after successful deployment, you can test the API using your favorite API client tool.
 
 ## Todos and improvements
 
 This version of the Employee API is in a very early stage and will be enhanced. Please find currently open
 points, which will be fixed in the near future, below.
 
 * Add missing IaC scripts (Terraform) to deploy the needed services
 * Add artifacts for deployment automation
 * Fix Failing test (JUnit and API Contract tests)
 * Enhance implementation, so that also non-happy paths are covered
 * Data is kept in-memory without proper persistence (add persistence support)