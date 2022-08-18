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
  kubectl -n kubernetes-dashboard create token admin-user
  ```
5. Run the proxy
   ```bash
   kubectl proxy
   ```
6. Open Dashboard in browser
  <http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login>

7. Paste the token you created in 4.
