resource "kubernetes_namespace" "app_namespace" {
  metadata {
    name = "app"
  }
}


resource "helm_release" "argocd" {
  name  = "argocd"

  repository       = "https://argoproj.github.io/argo-helm"
  chart            = "argo-cd"
  namespace        = "argocd"
  version          = "5.29.1"
  create_namespace = true

}

resource "helm_release" "argocd_apps" {
  name  = "argocd-apps"

  repository       = "https://argoproj.github.io/argo-helm"
  chart            = "argocd-apps"
  namespace        = "argocd"
  version          = "1.0.0"
  create_namespace = true

  values = [
    file("resources/applications.yaml")
  ]

  depends_on = [helm_release.argocd]
}