resource "helm_release" "grafana" {
  name  = "grafana"

  repository       = "https://grafana.github.io/helm-charts"
  chart            = "grafana"
  namespace        = "monitoring"
  version          = "6.54.0"
  create_namespace = true

  values = [
    file("resources/grafana.yaml")
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

resource "helm_release" "prometheus" {
  name  = "prometheus"

  repository       = "https://prometheus-community.github.io/helm-charts"
  chart            = "prometheus"
  namespace        = "monitoring"
  version          = "22.4.1"
  create_namespace = true

}