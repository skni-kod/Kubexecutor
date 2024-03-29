# Kubexecutor

A code execution sandbox running natively on Kubernetes. Written in Kotlin. 
The project leverages the power of Kubernetes orchestration and the security capabilities
of Kata containers to create a ~~robust and secure~~(one day) environment for executing untrusted code.

The project uses Kata containers as a runtime environment, which provide lightweight, secure and isolated containerized
virtual machines that offer an additional layer of protection compared to traditional container runtimes.

[Try it out here.](https://kube.skni.edu.pl/)

## Building images
To build service images, use defined Github Actions.

## Setting up dev cluster with k3s and Kata Containers
We'll be using kata-containers stable-3.1.
1. Setup clean ubuntu-server or some other base OS
2. `sudo apt-get update && sudo apt-get upgrade && sudo apt-get install git`
3. `git clone https://github.com/kata-containers/kata-containers.git`
4. `git checkout stable-3.1`
5. `curl -sfL https://get.k3s.io | sh -`
6. Wait till k3s is up and running (check with `kubectl get nodes`)
7. `cd /kata-containers/tools/packaging/kata-deploy`
8. `kubectl apply -f kata-rbac/base/kata-rbac.yaml`
9. `kubectl apply -k kata-deploy/overlays/k3s`
10. Wait till kata-containers is up and running (check with `kubectl -n kube-system wait --timeout=10m --for=condition=Ready -l name=kata-deploy pod`)
11. Add kata-containers runtime-classes: `kubectl apply -f https://raw.githubusercontent.com/kata-containers/kata-containers/main/tools/packaging/kata-deploy/runtimeclasses/kata-runtimeClasses.yaml`
12. Run an example kata-containers deployment: `kubectl apply -f https://raw.githubusercontent.com/kata-containers/kata-containers/main/tools/packaging/kata-deploy/examples/test-deploy-kata-dragonball.yaml`
13. Check if the example deployment works correctly: `kubectl describe deployment php-apache-kata-dragonball`

Source:
* https://docs.k3s.io/quick-start
* https://github.com/kata-containers/kata-containers/blob/stable-3.1/tools/packaging/kata-deploy/README.md

## Setting up ArgoCD and Grafana/Loki/Promtail stack for monitoring
1. Run `terraform apply` on a machine which keeps the state of the cluster.

## Accessing Grafana

```shell
kubectl port-forward svc/prometheus-grafana -n monitoring 3000:80

```

## Accessing ArgoCD

```shell
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

## Accessing PostgreSQL

```shell
kubectl port-forward svc/postgresdb -n postgres 5432:5432
```
Then log into database using any database client and secrets available on the cluster (namespace `postgres`).

## Deploying my version of Kubexecutor on dev env

In order to run my version of Kubexecutor on dev cluster:
1. Create a feature branch with desired changes.
2. Modify image tag version in `k8s/overlays/dev/kustomization.yaml`
3. Run Build & push Github Actions workflow of the modified service.
4. Connect to ArgoCD, enter kubexecutor app -> app details -> edit.
5. Change *Target Revision* to name of Your feature branch, i.e. `feature/test-deploy-from-branch`.
6. Sync the state of the application in ArgoCD.

After testing the changes:
1. Create a pull request with the changes and merge it.
2. Change *Target Revision* in ArgoCD to `HEAD`.