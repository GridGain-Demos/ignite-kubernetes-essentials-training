## Setting Up Kubernetes Dashboard

1. Apply the Dashboard configuration
  ```bash
  kubectl apply -f k8ui.yaml
  ```
2. Create the Service Account
  ```bash
  kubectl apply -f service-account.yaml
  ```
3. Apply the Cluster Role Binding
  ```bash
  kubectl apply -f cluster-role-binding.yaml
  ```
4. Create token
  ```bash
  kubectl -n kubernetes-dashboard get secret $(kubectl -n kubernetes-dashboard get sa/admin-user -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"
  ```
5. Open Dashboard in browser
  <http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login>

6. Paste the token you created in 4.
