### These labels affect how your app is viewed in Tanzu Observability and K8S
export K8S_NAMESPACE=tanzu-observability-demo
export K8S_APPLICATION=tanzu-observability-demo
export K8S_CLUSTER=tanzu-observability-demo
export K8S_LOCATION=xyz
export K8S_ENVIRONMENT=dev
export K8S_REPOSITORY=public.ecr.aws/tanzu_observability_demo_app/to-demo/

### Update with your Tanzu Observability by Wavefront info
export WAVEFRONT_BASE64_TOKEN=[YOUR BASE 64 ENCODED WAVEFRONT TOKEN HERE]
export WAVEFRONT_URL=http://longboard.wavefront.com/api

### Define for Tanzu Application Service Deploy
### otherwise leave empty
export TASDOMAIN=""
#example: export TASDOMAIN=-service.apps.internal

### Use default to deploy a proxy to the K8S namespace
### Edit for For Docker or Tanzu Application Service deploy
export WF_PROXY_HOST=${K8S_NAMESPACE}-wavefront-proxy  


