# Tanzu Observability demo-app
This is a polyglot demo app for showcasing Tanzu Observability.

#### TODO
1. Add UI repo link 

## Build, Package and Deploy
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
 - You must set `REPOSITORY_PREFIX` for your repository. Example:
 ```console
export REPOSITORY_PREFIX=192.168.1.8/demo-app
 ```
- Replace ***`192.168.1.8/demo-app`*** with your repository!
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
---
### Step One: Config Maps / Direct Ingestion or Wavefront Proxy

- The config maps are configured with a script with files from the `data` folder.

- Since the purpose of tacocat is to demo the power of Tanzu Observability, it can be configured to send metrics to Tanzu Observability via direct ingestion or by proxy. 
- Execute one of the following:
```console
./cm.sh direct
```
or
```console
./cm.sh proxy
```
- `cm.sh` will regen the `01-configmap-*.yaml` files.
- If you go direct, you'll need to edit the files and add your TO (Wavefront) details.
```console
./deploy/data/direct/inventory.conf
./deploy/data/direct/wf-config.yaml
```
- If you use the proxy, you'll need to configure a Kubernetes secret with your TO API token:
```console
kubectl apply -f namespace/.
kubectl create secret generic  wf-token -n tacocat
kubectl edit secrets wf-token  -n tacocat
```
- Your secrets file should look something like this:
```console
# Please edit the object below. Lines beginning with a '#' will be ignored,
# and an empty file will abort the edit. If an error occurs while saving this file will be
# reopened with the relevant failures.
#
apiVersion: v1
data:
  token: YOUR BASE64 ENCODED TOKEN HERE
kind: Secret
metadata:
  creationTimestamp: "2021-03-09T15:00:09Z"
  name: wf-token
  namespace: tacocat
  resourceVersion: "231537"
type: Opaque
```
---
 ### Step Two: Configure Yaml files with your repository:
- The yaml file are split into the `deploy`, `namespace` adn `services` folders. 
- This facilitates redeploying the apps in the `deploy` folder without changing the k8s service or namespace (easier redeploys).
- You'll need to edit (sed) the *.yaml files to have the location of your registry from the *Package* step like this:
```console
sed -i 's|<add your repository here>|192.168.1.8/demo-app|g' *.yaml
```
- Replace "***192.168.1.8/demo-app***" with **your** repository!
---
### Step Three: Deploy to K8S
- Initial deployment:
```console 
kubectl apply -f namespace/.
kubectl apply -f service/.
kubectl apply -f . 
```
- To redeploy (but not delete the namespace or service):
```
kubectl delete -f . 
kubectl apply -f . 
```
---
## Need to add link to UI repo

---

## That's it!
Please let use know how we can improve!