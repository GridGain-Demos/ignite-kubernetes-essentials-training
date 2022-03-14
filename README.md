# Project Template for the Apache Ignite and Kubernetes Training Course

This project is used for hands-on exercises of the
[Apache Ignite and Kubernetes: Deployment and Orchestration Strategies](https://www.gridgain.com/products/services/training/apache-ignite-and-kubernetes-deployment-and-orchestration-strategies)
training course. That's a free two-hour training session for developers, architects, and DevOps engineers who need to
deploy and orchestrate Apache Ignite in a Kubernetes environment.

Check the [schedule](https://www.gridgain.com/services/training) a join one of our upcoming sessions.
All the courses are delivered by seasoned Ignite community members.


## Setting Up Environment

1. Install Docker Desktop and `kubectl` tool.
2. Enable Kubernetes in [Docker Desktop's settings](https://docs.docker.com/desktop/kubernetes/)
3. Java Developer Kit, version 8 or later
4. Apache Maven 3.0 or later
5. Your favorite IDE, such as IntelliJ IDEA, or Eclipse, or a simple text editor.

## Clone The Project

1. Clone this project or download it as an archive:
    ```bash
    git clone https://github.com/GridGain-Demos/ignite-kubernetes-essentials-training.git
    ```

## Set Up Kubernetes for Ignite

Start the Ignite Kubernetes Service that is used inter-node communication needs:
1. Navigate to the project's config folder:
    ```bash
    cd {project_root}/ignite-kubernetes-essentials-training/config
    ```

2. Create a namespace for the training course:
     ```bash
     kubectl create namespace ignite-namespace
     ```

3. Start the Ignite Kubernetes service
    ```bash
    kubectl create -f ignite-service.yaml
    ```
4. Confirm the service is running:
    ```bash
    kubectl get services -n ignite-namespace
    ```

Next, create a Cluster Role and Service Accounts:
1. Create a service account:
    ```bash
    kubectl create sa ignite -n ignite-namespace
    ```
2. Create a Cluster Role:
    ```bash
    kubectl create -f cluster-role.yaml
    ```

## Start Ignite Cluster

1. Create a ConfigMap with persistent configuration:
    ```bash
    kubectl create configmap ignite-cfg-persistent -n ignite-namespace --from-file=persistent/ignite-node-cfg.xml
    ```
2. Start the cluster:
    ```bash
    kubectl create -f persistent/ignite-cluster.yaml
    ```   
3. Confirm the Ignite pods are running:
    ```bash
    kubectl get pods -n ignite-namespace
    ```
4. Double check with Ignite logs that a 2-node cluster was created:
    ```bash
    kubectl logs ignite-cluster-0 -n ignite-namespace
    ```
5. Attach your cluster to GridGain Control Center by going to `https://portal.gridgain.com/` and providing the
cluster's token ID from the logs.   

6. Active the persistent cluster with Control Center `https://portal.gridgain.com/clusters/list`

## Load Sample Database

1. Connect to the first cluster pod via bash:
    ```bash
    kubectl exec --stdin --tty ignite-cluster-0 -n ignite-namespace -- /bin/bash
    ```
2. Go to the `bin` folder:
    ```bash
    cd bin/
    ```
3. Connect to the cluster via SQLLine tool:
    ```bash
    ./sqlline.sh -u jdbc:ignite:thin://127.0.0.1/
    ```
4. Load the World database:
    ```bash
    !run ../examples/sql/world.sql
    ```
5. Quit from SQLLine:
    ```bash
    !q
    ```
6. Quit from bash:
    ```bash
    exit
    ```
7. Refresh the `Storage Metrics` dashboard on the Control Center side to confirm that the data was loaded.

# Create Dashboard for Storage Metrics Monitoring

1. Go to the Dashboard screen of Control Center

2. Create a new Dashboard named `Storage Metrics`

3. Add the following widgets to the Dashboard:
    * Off-heap memory: `Metrics(table)`->`PhysicalMemorySize` (for the `default` region)
    * Storage size: `Metrics(table)`->`StorageSize` (for the `datastorage`)
    * WAL size: `Metrics(table)`->`WALTotalSize`
    * Checkpointing duraion: `Metrics(chart)`->`LastCheckpointDuration`
    * Other metrics: https://www.gridgain.com/docs/tutorials/management-monitoring/ignite-storage-monitoring

## Connect With External Applications

In this section, you're connecting to the K8 Ignite cluster with external APIs and applications (those that are not deployed
in the same K8 environment).

### Query Data With Ignite REST API

1. Open up your preferred browser and check the cluster state via the Ignite REST API:
    ```bash
    http://localhost:8080/ignite?cmd=state
    ```
   Note, the `localhost` is the `external-IP` of the Ignite Kubernetes Service.

2. Select the count of Cities:
    ```bash
    http://localhost:8080/ignite?cmd=qryfldexe&pageSize=10&cacheName=City&qry=SELECT%20count(*)%20From%20City
    ```  
3. Run the `SampleJavaApp` located in this project.   

## Run Java Thin Client

1. Open the Java project in your IDE

2. Execute the thin client class
  ```
  com.gridgain.example.SampleThinClient
  ```

## Deploy Thick Client in kubernetes

1. Build the project
  ```bash
  mvn clean package
  ```

2. Create the Docker image
  ```bash
  docker build -t ignite-thick-client .
  ```
3. Create the thick client pods
  ```bash
   kubectl apply -f config/thick-client.yaml
   ```

## Clear Project Resources

1. Remove all the resources associated with this project:
    ```bash
    kubectl delete namespace ignite-namespace
    ```  
2. Remove the ClusterRole:
    ```bash
    kubectl delete clusterrole ignite -n ignite-namespace
    ```
3. Remove the ClusterRole binding:
    ```bash
    kubectl delete clusterrolebinding ignite -n ignite-namespace
    ```

Note, use this command if the termination of an Ignite pod is stuck:
`kubectl delete pod ignite-cluster-1 --grace-period=0 --force -n ignite-namespace`
