# Tanzu Observability demo-app
This is a polyglot demo app for showcasing Tanzu Observability.

----
To view in Tanzu Observability by Wavefront you will need:
* A free Tanzu Observability account: 
  * Free trial: https://tanzu.vmware.com/observability
* Base64 encoded API token and the URL for your instance: 
  * Docs here: https://docs.wavefront.com/wavefront_api.html#generating-an-api-token
----
## Run

### Kubernetes

* Deploy with helm or kubectl from ```public.ecr.aws/tanzu_observability_demo_app/to-demo```
* You can also build, package and push to your registry and deploy from there.

#### Deploy with Helm
##### Clone the repo:
```console
git clone https://github.com/wavefrontHQ/demo-app.git
cd demo-app/deploy/helm
```
Edit  `values.yaml` to match your environment or use the defaults and execute helm:
```console
vi values.yaml
```
Minimally update:
```console
wavefront:
 base64_token: 
 url: 
 ```
 
Install with helm:

```console
helm install tacocat-demo .
```
Verify the pods are running:
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

## Configure, Build, Package and Deploy

For those that prefer to build and host their own containers

---

#### Configure

- Configure the `deploy/src/values.sh` file with your settings:
```
export K8S_NAMESPACE=tacocat
export K8S_APPLICATION=tacocat
export K8S_CLUSTER=cluster1
export K8S_LOCATION=americas
export K8S_REPOSITORY=public.ecr.aws/tanzu_observability_demo_app/to-demo/
export WAVEFRONT_BASE64_TOKEN=<YOUR BASE64 ENCODED TOKEN HERE>
```
The above variables will be used by below scripts to fill in the values to setup the \*.yaml files. `K8S_REPOSITORY` is the repository URL for container images which needs to be specified in order for the deployment to properly download the images.
You can use the provided K8S_REPOSITORY to deploy and avoid the build and package steps.

- Generate the the yaml files
```console
cd deploy/src
./cm.sh 
```
- `cm.sh` creates the 01-app-config-*.yaml files and runs `create-yaml.sh` which uses `envsubst` and `values.sh` to configure all of the *yaml files
- *The above step must be run before building the images as it also creates `applicationTag.yaml` files used by the services.*

---

Skip to Deploy if you do not want to build, package and push to your registry/repository.

---
### Build
- Build the Java services - from the root folder run:
```console
mvn clean package
```
- The .Net service (Payments) and the Golang service (inventory) are built when creating the docker container in the Package step.
 ---

### Package 
*Note: This step will attempt to push to the registry referenced above!*
 - Create docker images and push to registry.
 - `deploy/src/values.sh` is referenced here to provide the registry and app tags.
- Build containers and push to your registry:
 ```console
cd ../images-k8s 
./all.sh
```
---
### Deploy
```console
cd deploy
```
- The yaml file are split into the `deploy`, `namespace` and `services` folders. 
- This facilitates redeploying the apps in the `deploy` folder without changing the k8s service or namespace (easier redeploys).
- Deploy the namespace first:
```
kubectl apply -f namespace/
```
- Deploy the Wavefront proxy and shopping service:
```
kubectl apply -f services/
```
- Deploy the app:
``` 
kubectl apply -f . 
```
- To redeploy (but not delete the namespace or service):
```
kubectl delete -f . 
kubectl apply -f . 
```
---
## That's it!
Please let us know how we can improve!

---
#### TODO
1. Add build containers
2. Add UI repo link 

