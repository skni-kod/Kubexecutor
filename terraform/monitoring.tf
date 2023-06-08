resource "helm_release" "kube-prometheus-stack" {
  name  = "prometheus"

  repository       = "https://prometheus-community.github.io/helm-charts"
  chart            = "kube-prometheus-stack"
  namespace        = "monitoring"
  version          = "46.8.0"
  create_namespace = true

  values = [
    file("resources/kube-prometheus-stack.yaml")
  ]

}

resource "helm_release" "loki" {
  name  = "loki"

  repository       = "https://grafana.github.io/helm-charts"
  chart            = "loki"
  namespace        = "monitoring"
  version          = "5.1.0"
  create_namespace = true

  values = [
    file("resources/loki.yaml")
  ]

}

resource "helm_release" "promtail" {
  name  = "promtail"

  repository       = "https://grafana.github.io/helm-charts"
  chart            = "promtail"
  namespace        = "monitoring"
  version          = "6.10.0"
  create_namespace = true

}