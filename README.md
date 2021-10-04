# Tanzu Observability demo-app
This is a polyglot demo app for showcasing Tanzu Observability.

You can configure locally and then deploy from ECR registry.
You can also build, package and push to your registry and deploy from there.

## Configure, Build, Package and Deploy
---

### Configure

- Configure the `deploy/src/values.sh` file with your settings:
```
export K8S_NAMESPACE=tacocat
export K8S_APPLICATION=tacocat
export K8S_CLUSTER=cluster1
export K8S_LOCATION=americas
export K8S_REPOSITORY=public.ecr.aws/r8e5f6o2/to-demo
export WAVEFRONT_BASE64_TOKEN=<YOUR BASE64 ENCODED TOKEN HERE>
```
The above variables will be used by below scripts to fill in the values to setup the \*.yaml files. `K8S_REPOSITORY` is the repository URL for container images which needs to be specified in order for the deployment to properly download the images.
You can use the provided K8S_REPOSITORY to deploy and avoid the build and package stages.

- Generate the the yaml files
```console
cd deploy/src
./cm.sh 
./create-yaml.sh
```
- `cm.sh` creates the 01-app-config-*.yaml files 
- `create-yaml.sh` uses `envsubst` and `values.sh` to configure all of the *yaml files
- *Note: `create-yaml.sh` must be run before building the images as it also creates `applicationTag.yaml` files used by the services.

---

Skip to Deploy if you do not want to build, package and push to your registry/repository.

---
### Build
- Build the Java services - from the root folder run:
```console
mvn package
```
- Build the .Net service:
```console
cd payments; dotnet build
```
- Build the Golang service: 
 ```console
 cd inventory; make
 ```
 ---

### Package
 - Create docker images and push to registry.
 - `deploy/src/values.sh` is referenced here to provide the repository.
- Build containers and push to your repository:
 ```console
cd images; 
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
```console 
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
1. Add UI repo link 

