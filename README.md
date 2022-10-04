# Tanzu Observability Demo Application

This project is a polyglot demo application with several services to show you how to send your data and view the data on Tanzu Observability.

* [Prerequisites](#Prerequisites)
* [Send Data](#send-data)
  * [Deploy with Helm](#deploy-with-helm)
  * [Build and Run the Application](#build-and-run-the-application)
* [View Data](#view-data)
* [Getting Support](#getting-support)

## Prerequisites

To view data in Tanzu Observability by Wavefront, you need:
* A Tanzu Observability account. If you don't have an account, register for the [free trial](https://tanzu.vmware.com/observability).
* Get the API token and the URL of your instance. For more information, see [Managing API Tokens](https://docs.wavefront.com/wavefront_api.html#generating-an-api-token).

## Send Data

You can:

* Deploy with helm or kubectl using the preconfigured containers on ```public.ecr.aws/tanzu_observability_demo_app/to-demo```
* You can also build, package and push to your registry and deploy from there.

### Deploy with Helm

#### Prerequisite

You need an environment configured to run Kubernetes. See [Install Tools on the Kubernetes Documentation](https://kubernetes.io/docs/tasks/tools/) for details.

#### Configure and Run the Application

1. Clone the repo:
    ```console
    git clone https://github.com/wavefrontHQ/demo-app.git
    cd demo-app/deploy/helm
    ```
2. Edit `values.yaml` to match your environment:
    ```console
    vi values.yaml
    ```
3. Enter the API Token and URL of your instance:
    ```console
    wavefront:
    token: 
    url: 
    ```
4. Optionally, to send logs to Tanzu Observability with `fluentd`, update `values.yaml` and set logs to `true`:
    ```console
    logs:
      enabled: true
    ```

5. Install with Helm:

    ```console
    helm install tacocat-demo .
    ```
6. Verify that the pods are running.

    Example: 
    ```console
    k get pods -n  tanzu-observability-demo

    ~/l/d/d/helm ❯❯❯ k get pods -n  tanzu-observability-demo
    NAME                                                       READY   STATUS    RESTARTS   AGE
    delivery-blue-764995948c-7g6vn                             1/1     Running   0          14m
    delivery-green-867f978c5f-gftdt                            1/1     Running   0          13m
    inventory-blue-b86c59d8d-sv2gg                             1/1     Running   0          13m
    inventory-green-85679dc4c6-lrhd5                           1/1     Running   0          13m
    loadgen-65676c865b-hkffn                                   1/1     Running   0          12m
    notification-blue-5668c6df94-cpvlm                         1/1     Running   0          13m
    notification-green-76895b75d6-tqr84                        1/1     Running   0          14m
    packaging-blue-5775f7c86f-tccsc                            1/1     Running   0          14m
    packaging-green-7cb5644df7-f766l                           1/1     Running   0          14m
    payments-blue-7486b5bd59-8rggp                             1/1     Running   0          14m
    payments-green-5bdf5869bc-447xf                            1/1     Running   0          14m
    printing-blue-9fb48997d-bwsqx                              1/1     Running   0          14m
    printing-green-7f66cd9bfd-npglt                            1/1     Running   0          14m
    shopping-blue-68b89984fd-x52bg                             1/1     Running   0          13m
    shopping-green-666dcc6966-lgmrp                            1/1     Running   0          14m
    styling-blue-64dbc49f5b-2wrg4                              1/1     Running   0          14m
    styling-green-75789ddc76-k77nx                             1/1     Running   0          14m
    tanzu-observability-demo-wavefront-proxy-65c544fdd-6959p   1/1     Running   0          14m
    warehouse-blue-6cb8f5c988-ltpgv                            1/1     Running   0          14m
    warehouse-green-5b65b7d764-z8g6n                           1/1     Running   0          14m
    ```
---

### Build and Run the Application

You can build the application and run it locally on your machine or on Docker. 

#### Prerequisites
* Bash or zsh 
* envsubst

#### Configure the Application

1. Clone the repo:
  ```console
  git clone https://github.com/wavefrontHQ/demo-app.git
  cd demo-app
  ```
2. Configure  `deploy/src/values.sh`  with your settings:
  ```
  export K8S_NAMESPACE=tacocat
  export K8S_APPLICATION=tacocat
  export K8S_CLUSTER=cluster1
  export K8S_LOCATION=americas
  export K8S_REPOSITORY=public.ecr.aws/tanzu_observability_demo_app/to-demo/
  export WAVEFRONT_BASE64_TOKEN=<YOUR BASE64 ENCODED TOKEN HERE>
  export WF_PROXY_HOST=${K8S_NAMESPACE}-wavefront-proxy  
  ```
  > * The variables in `values.sh` are used to configure values for the `yaml` files. 
  > * `K8S_REPOSITORY` is the repository URL for container images. The repository URL needs to be specified for the deployment to properly download the images.
  > * If you use the value that is already defined for `K8S_REPOSITORY`, skip the build and package steps.
  > If running locally with `docker-compose`, you do not need the `K8S_REPOSITORY` settings:
    
    ```
    #export K8S_REPOSITORY=public.ecr.aws/tanzu_observability_demo_app/to-demo/
    export WF_PROXY_HOST=wavefront-proxy
    ```
3. Generate the YAML files.
  ```console
  cd deploy/src
  ./cm.sh 
  ```
  > - `cm.sh` creates the 01-app-config-*.yaml files and runs `create-yaml.sh` which uses `envsubst` and `values.sh` to configure all of the `*yaml` files.
  > - You must run this step before building the images as it also creates `applicationTag.yaml` files used by the `services.*`.

#### Build

Build the Java services from the root folder:
  ```console
  mvn clean package
  ```
  > The .Net service (Payments) and the Golang service (inventory) are built when creating the docker container in the Package step.

#### Package 
> *Note: This step pushes data to the registry defined in* `K8S_REPOSITORY`. <br>
>
> To run locally with `docker-compose`, `K8S_REPOSITORY` should be undefined or empty.
 
Create docker images and push them to the registry (if `K8S_REPOSITORY` is set).

```console
cd ../images-k8s 
./all.sh
```

#### Deploy

You can deploy they application using `docker-compose` or `kubectl`.

##### Deploy with `docker-compose`

1. Navigate to the `docker-compose` directory.
    ```console
    cd deploy/docker-compose
    ```
2. Edit `docker-compose.yaml` and update the `WAVEFRONT PROXY` settings:
    ```
    wavefront-proxy:
        image: projects.registry.vmware.com/tanzu_observability/proxy:latest
        container_name: wavefront-proxy
        environment: 
          - WAVEFRONT_URL=https://[YOUR TENANT].wavefront.com/api
          - WAVEFRONT_TOKEN=[YOUR API KEY]
    ```
3. Deploy the application.
    1. Deploy using local containers:
        ```
        docker-compose up -d
        ```
    1. Or deploy using hosted containers:
        ```
        export export K8S_REPOSITORY=public.ecr.aws/tanzu_observability_demo_app/to-demo/; docker-compose  up -d
        ```
4. Verify that the containers are running:
    ```
    ~/l/d/d/docker-compose ❯❯❯ docker-compose ps
                                                                                                              ✘ 130 
              Name                        Command               State                         Ports                       
    ----------------------------------------------------------------------------------------------------------------------
    delivery                   java -jar /delivery.jar -- ...   Up                                                        
    docker-compose_loadgen_1   java -jar /loadgen.jar sho ...   Up                                                        
    inventory                  /inventory /conf/inventory ...   Up                                                        
    notfication                java -jar /notification.ja ...   Up                                                        
    packaging                  java -jar /packaging.jar / ...   Up                                                        
    payments                   dotnet run --no-build -p / ...   Up                                                        
    printing                   java -jar /printing.jar /c ...   Up                                                        
    shopping                   java -jar /shopping.jar se ...   Up      0.0.0.0:50050->50050/tcp, 0.0.0.0:50150->50150/tcp
    styling                    java -jar /styling.jar ser ...   Up                                                        
    warehouse                  python3 manage.py runserve ...   Up                                                        
    wavefront-proxy            /bin/bash /opt/wavefront/w ...   Up      2878/tcp, 3878/tcp, 4242/tcp  ```
    ```

##### Deploy With `kubectl`
- The yaml files are split into `deploy`, `namespace`, and `services` folders. 
- With this folder structure, you can redeploy the apps in the `deploy` folder without changing the Kubernetes service or namespace.

Follow these steps:

1. Deploy the namespace first:
    ```
    kubectl apply -f namespace/
    ```
1. Deploy the Wavefront proxy and shopping service:
    ```
    kubectl apply -f services/
    ```
1. Deploy the app:
    ``` 
    kubectl apply -f . 
    ```
1.  To redeploy (but not delete the namespace or service):
    ```
    kubectl delete -f . 
    kubectl apply -f . 
    ```

## View Data

Once the data is sent, you can:
- Create charts and dashboards to monitor data.
- Create alerts and get notified when the system notices data anomalies.
- Get an overview of how the applications and services are linked using the Application Map.
- Explore the context and the details of your application’s traces using the Traces Browser.
- See the logs and find the root cause of issues using the Logs Browser.

See our [documentation](https://docs.wavefront.com/index.html) for details.

## Getting Support
Please let us know how we can improve!

If you have problems with this application, let us know by creating a GitHub issue. 