# ignite-kubernetes-essentials-training
Project for the Apache Ignite and Kubernetes Training Course


## Setting Up Environment

1. Install Docker Desktop and `kubectl` tool.
2. Enable Kubernetes through Docker Desktop's settings: https://docs.docker.com/desktop/kubernetes/
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

1. Create a ConfigMap:
    ```bash
    kubectl create configmap ignite-cfg-memory-only -n ignite-namespace --from-file=memory-only/ignite-node-cfg.xml 
    ```
2. Start the cluster:
    ```bash
    kubectl create -f memory-only/ignite-cluster.yaml
    ```
3. Confirm the Ignite pods are running:
    ```bash
    kubectl get pods -n ignite-namespace
    ```
4. Double check with Ignite logs that a 2-node cluster was created:
    ```bash
    kubectl logs ignite-cluster-0 -n ignite-namespace
    ```
5. Attach your cluster to GridGain Control Center by going to `portal.gridgain.com` and providing the
cluster's token ID from the logs.

## Scale Out the Ignite Cluster

1. Scale the cluster by adding the 3rd node:
    ```bash
    kubectl scale sts ignite-cluster --replicas=3 -n ignite-namespace
    ```
2. Check with the Control Center Dashboard that you have the 3-node cluster.

## Start the Cluster With Native Persistence

1. Stop the memory-only cluster:
    ```bash
    kubectl delete sts ignite-cluster -n ignite-namespace
    ```
2. Create a ConfigMap with persistent configuration:
    ```bash
    kubectl create configmap ignite-cfg-persistent -n ignite-namespace --from-file=persistent/ignite-node-cfg.xml 
    ```
3. Start the cluster:
    ```bash
    kubectl create -f persistent/ignite-cluster.yaml
    ```   
4. Confirm the Ignite pods are running:
    ```bash
    kubectl get pods -n ignite-namespace
    ```
5. Double check with Ignite logs that a 2-node cluster was created:
    ```bash
    kubectl logs ignite-cluster-0 -n ignite-namespace
    ```
6. Attach your cluster to GridGain Control Center by going to `https://portal.gridgain.com/` and providing the
cluster's token ID from the logs.   

7. Active the persistent cluster with Control Center `https://portal.gridgain.com/clusters/list`

## Create Dashboard for Storage Metrics Monitoring

1. Go to the Dashboard screen of Control Center

2. Create a new Dashboard named `Storage Metrics`

3. Add the following widgets to the Dashboard:
    * Off-heap memory: `Metrics(table)`->`PhysicalMemorySize` (for the `default` region)
    * Storage size: `Metrics(table)`->`StorageSize` (for the `datastorage`)
    * WAL size: `Metrics(table)`->`WALTotalSize`
    * Checkpointing duraion: `Metrics(chart)`->`LastCheckpointDuration`
    * Other metrics: https://www.gridgain.com/docs/tutorials/management-monitoring/ignite-storage-monitoring
    

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

## Create Cluster Snapshot

Go to the `Snapshots` screen of Control Center and create a sample snapshot of your Kubernetes cluster.

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
