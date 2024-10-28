package org.sa;

public class OneLiners {

  private String qa =
      "DRT - Disaster Recovery Tests - process, that enables service to prepare and test support functions for the actual Disaster Recovery.\n" +
      "Canary deployment - new version app is gradually rolled out to a small subset of users or servers before being deployed to the entire infrastructure.\n" +
      "Test pilot - limited-scale implementation or trial of new version. Conducted before a full-scale release to a wider audience. Allows testing, gathering feedback, identify issues or improvements.\n" +
      "Feature flag - feature toggle.\n" +
      "A/B testing - split testing - compare two versions (A and B) of a webpage.\n" +
      "Chaos engineering - injecting controlled instances of failure into a system to identify vulnerabilities.\n" +
      "KPI - key performance indicator.\n" +
      "PPTE - pre production test environment.\n" +
      "Provisioning - making hardware, software, or data, available and ready for use.\n" +
      "TDM - Test Data Management.\n" +
      "Staging Environment - closely resembles the production environment without affecting actual production data.\n" +
      "Subsetting data - selecting and using a representative portion of a larger dataset.\n" +
      "Data Pseudonymization - protecting privacy by replacing or encrypting personally identifiable information (PII) with artificial identifiers (pseudonyms).\n" +
      "Agile Test Quadrants - concept categorizing types of testing. Based on two axes: technology-facing and business-facing.\n" +
      "Test data management for entreprise - meeting the needs of an organization rather than specific individual or teams needs.\n" +
      "GPA - general purpose app.\n" +
      "Integrated test environment -  test environment that closely resembles the production environment.\n" +
      "SLI - Service Level Indicator - Metric measuring system performance.\n" +
      "SLO - Service Level Objective - Target level of performance for reliability.\n" +
      "Error budget - Maximum allowable errors metric in a system within a specified time frame for reliability.\n" +
      "Stub - small piece of code that stands in for a larger piece of code. Simulates non implemented functionality. Isolates parts of program for testing.\n" +
      "Penetration testing - authorized simulated cyberattack on a computer system, performed to evaluate the security of the system.\n" +
      "Test integration - combining individual software components or modules and testing them together, if they work when integrated into a larger system.\n" +
      "Unit Testing - Testing individual units of code in isolation (AKA component testing).\n" +
      "Regression Testing - Testing to ensure new changes don't negatively impact existing functionality.\n" +
      "Performance Testing - Testing the performance and scalability of a software application under various conditions.\n" +
      "Integration Testing - Ensures integrated software components work together as intended.\n" +
      "Functional Testing - Verifies that software features align with specified requirements.\n" +
      "UAT - User Acceptance Testing - validates software from end users' perspective for alignment with needs.\n" +
      "Security Testing - identifies vulnerabilities and security risks in the software.\n" +
      "Usability Testing - evaluates user-friendliness and overall user experience.\n" +
      "Compatibility Testing - checks software performance across different environments and setups.\n" +
      "Exploratory Testing - flexible approach to uncover unexpected defects and understand behavior.\n" +
      "Localization and Internationalization Testing - adapts software for different languages and tests for global use.\n" +
      "Accessibility Testing - ensures software usability for individuals with disabilities.\n" +
      "DDoS attacks - Distributed denial-of service attacks.\n" +
      "XSS - Cross-Site Scripting - is a security vulnerability that allows attackers to inject malicious scripts into web pages viewed by other users.\n";

  private String containerisation =
      "Worker node - computing node dedicated to running the containerized applications, has its own CPU, memory, and other resources.\n" +
      "Master node - responsible for managing the overall cluster state.\n" +
      "Cluster - all nodes.\n" +
      "Port - communication endpoint.\n" +
      "Endpoint - specific address or location in a network where communication or data transfer can occur.\n" +
      "Container - isolated software package that contains everything needed to run an application: code, dependencies, libraries, and system settings. is a running instance of image.\n" +
      "Pod - smallest deployable unit. It can hold one or more containers.\n" +
      "Container runtime - software component - executes the processes within individual containers and provide isolation between them.\n" +
      "Runtime - 1. environment providing the necessary components to execute an app. 2. period during which a program is executing.\n" +
      "Container management platform - software system - manages the deployment, scaling, and orchestration of containers across a cluster.\n" +
      "Docker - container runtime.\n" +
      "Rancher - container management platform - manages workloads, deploys, scales containerized applications. Uses container runtime Kubernetes (Docker or other). Sets it's clusters.\n" +
      "Is Rancher a container orchestration platform?  - not a container orchestration platform itself, but it is a management platform for container orchestration and Kubernetes. Provides a user-friendly interface and tools for deploying, configuring.\n" +
      "Andromeda functions - Application Development Platform - with managed services such as containers, monitoring, logging, traceability, caching, etc., for developing, deploying and operating cloud native applications.\n" +
      "Andromeda context - Application Development Platform - runs your applications inside containers that is deployed in a Kubernetes cluster. Andromeda is a part of OpenShift. And OpenShift is built on Kubernetes.\n" +
      "Kubernetes - container orchestration platform.\n" +
      "OpenShift - is built on top of Kubernetes and extends its capabilities with additional features for application development, deployment, and operations.\n" +
      "Throttling - intentional limitation or control of certain resources, actions, or processes within an app or a system.\n" +
      "Garbage collection - is an automated process that identifies and frees up memory.\n" +
      "Microservice - independent part of application for a specific function.\n" +
      "Namespace (1) - In networking, namespaces can refer to isolated environments where different systems or applications can have their own unique identifiers without conflicting with others.\n" +
      "Namespace (2) - way to divide cluster resources between multiple users (or multiple projects or teams) on a single cluster.  Within a namespace, you can have pods, services, deployments, and other resources.\n" +
      "PV - Persistent Volume - storage resource that exists independently of a container. Managed by administrator/ platform.\n" +
      "PVC - Persistent Volume Claim - request for PV storage by a user.\n" +
      "Helm - package manager for Kubernetes - setts up and manages applications - uses pre-configured packages containing all the necessary resources: services, deployments, and configuration files, needed to run a specific application.\n" +
      "Helm chart - package / set of files. Organized into a directory structure. Often reference container images as part of their configurations.\n" +
      "Container image - executable package that includes everything needed to run a piece of software, including the code, runtime, libraries, and system tools.\n" +
      "Base Java Docker image - serves as the foundation for running Java applications within a Docker container.  Provides the foundational environment and Java runtime for running Java applications within a container.\n" +
      "YAML - (YAML Ain't Markup Language) -  is a human-readable data serialization format. Like XML or JSON.\n" +
      "Haselcast - software to distribute data and computing load over nodes. Provides parallel processing and in-memory data grid.\n" +
      "IMDG - An in-memory data grid - distributed computing technology that stores and manages data in RAM memory.\n" +
      "DC - datacenter - building to house computer systems. includes: telecommunications, storage systems, backup power supplies, redundant data communications connections, etc.\n" +
      "Cloud - services over the internet, such as storage, processing power, databases, networking, software, and analytics. Instead of relying on local devices.\n" +
      "SSH - Secure Shell key - pair of cryptographic keys (public and private) - used to authenticate and secure communication over a network.\n" +
      "NFS - Network File System - for accessing files over a network as if they were on a local file system.\n";

  private String apis =
      "API - Application Programming Interface - set of rules and protocols that allows different software applications to communicate.\n" +
      "Using APIs - exchanging data and functionality using predefined rules.\n" +
      "API Gateway - intermediary server, that provides a single entry point for clients to access various APIs and services, while also offering features that enhance the management, security, and performance of those APIs.\n" +
      "Swagger - framework that simplifies the process of designing, documenting, and testing RESTful web APIs.\n" +
      "REST - Representational State Transfer - API architecture standard.\n" +
      "RESTful - 1. explicitness(statelessness) 2.client server 3.standard HTTP methods (GET, POST, PUT, DELETE) 4.standard representations (JSON, XML, HTML) 5. layers(controller(endpoints), service(business), data access).\n" +
      "URL - Uniform Resource Locators.\n";

  private String logs =
      "Kibana - log and data visualization and exploration tool. Used in conjunction with OpenShift (or other).\n" +
      "Logread - our system where logs are being stored in files.\n" +
      "Loki - log aggregation system and time-series database that is part of the Grafana.\n" +
      "ELK - is an acronym that stands for Elasticsearch, Logstash, and Kibana.\n" +
      "Elasticsearch - search and analytics engine. Handles and analyzes large volumes of data in real-time.\n" +
      "Logstash - data processing pipeline. Ingests, transforms, and enriches data. Often used for logs.\n" +
      "Full-text search - searching technique. Searches all content. Scores matching. Understands context, synonyms. Ignores overly common terms. Reduces words to steam: runner -> run.\n" +
      "Distributed computing - interconnected computers that work as a single system. Computers/nodes are in different locations and communicate with each other over a network.";

  private String esealing =
      "E-seal - a proof that the document has integrity (not tampered).\n" +
      "ESealing narrative - tellers use the STAR application to upload the prepared documents and add e-seals to them. Users access these documents through iBank.\n" +
      "Tamper - make illegal and/or damaging alteration.\n" +
      "Seal Service - sorts and groups documents. Works on Andromeda.\n" +
      "Signature Container Service - adds e-seals to documents. Works on Rancher 2.\n" +
      "E-archive - stores documents. Works on Rancher 2.\n";

  private String databases =
      "Core - marks that database is main. But in our case there are only core databases so it's a bit meaningless, e.g. \"test_lt_core\".\n" +
      "Partition and sample - part of database table.\n" +
      "NOT IN (sql) - operator, !=\n" +
      "DBA - database administrators.\n" +
      "CTE - Common Table Expression - result set of a query which exists temporarily and for use only within the context of a larger query.\n";

  private String regulations =
      "DORA - Digital Operational Resilience Act - EU rules rules for financial institutions to manage unforeseen data disruption incidents.\n" +
      "GDPR - General Data Protection Regulation - European Union regulation on information privacy.\n" +
      "Personal data retention - storing and maintaining individuals' personal information for a specific period.\n" +
      "EDD - Enhanced Due Diligence - more thorough investigation of customer - beyond the standard KYC procedures.\n" +
      "Tampering - making illegal or/and damaging alterations.\n" +
      "DSS - data security standard.\n" +
      "PCI DSS - Payment Card Industry Data Security Standard - ensures the secure handling of credit card information during payment transactions.\n" +
      "AFC - anti financial crime.\n" +
      "Java EE - Java Enterprise Edition -  platform, which provides a set of specifications for building enterprise-level applications.\n" +
      "Enterprise-level - scale, complexity, and requirements to meet the needs of large organizations.\n";

  private String softwareEngineering =
      "ORM - Object-Relational Mapping - interaction between a database and OOP. database operations are performed using objects and classes rather than raw SQL queries. E.g.: Hibernate for Java.\n" +
      "Hash - unique representation of the input data. Used for encryption, integrity verification, indexing etc. Deterministic, fixed output size, irreversible, collision resistant.\n" +
      "Deterministic - same input will always produce the same outcome.\n" +
      "Irreversible - not possible obtain the original input from the outcome.\n" +
      "Collision resistant - different inputs should not produce the same outcome.\n" +
      "Memoization technique - storing the results of expensive function calls and return the cached result when the same inputs occur again.\n" +
      "AD - architecture description.\n" +
      "FDP - frontend development platform (example: React).\n" +
      "Deploying - making app available on a target environment. For example production environment.\n" +
      "VPN - Virtual Private Network - encrypted and tunneled connection to trusted remote server, where your identity is hidden, but all the data in the internet accessed.\n" +
      "DevOps - practices, principles, and cultural philosophies. The primary goal is to automate the process of software delivery and infrastructure changes.\n" +
      "SSL (1) - Secure Sockets Layer - standard security protocol that encrypts the communication between a web server and a browser to protect sensitive information.\n" +
      "SSL (2) - Secure Sockets Layer - protects communication over a network or internet. Encrypts the data between browser and website server. Successor - Transport Layer Security - TLS.\n" +
      "Code integration - combining code changes from multiple contributors into a shared repository.\n" +
      "PR - public release.\n" +
      "DNS - Domain Name System - maps user - friendly domain names to numeric IP addresses.\n" +
      "Subnet - subnetwork - segment of a larger network. Subnetting improves network performance, security, and organization.\n" +
      "Synchronous - each next task starts when previous one ends.\n" +
      "Asynchronous - each next task starts without previous one ending. Task become concurrent.\n" +
      "Encapsulation - Hiding internal elements of object or service. Provides clear separation. Improves maintainability.\n" +
      "Technical debt - accumulated code imperfections.\n" +
      "SwiftUI - user interface framework developed by Apple for building apps across iOS, macOS, watchOS, and tvOS.\n" +
      "Unix - operating system. Linux is a Unix - like, but not a direct descendant.\n" +
      "CHROMIUM - base upon which many modern web browsers are built.\n" +
      "Jenkins - automating tasks like code integration, testing, and deployment.\n" +
      "Microsoft Azure - cloud computing platform.\n" +
      "Aspect - oriented programming - way to separate functionality. Example: logging specific method can be triggered from separate class.\n" +
      "Boilerplate - sections of code that have to be included in many places with little or no alteration.\n" +
      "Framework - reusable and extensible structure that helps building applications.\n" +
      "Factory - patterns that provide an interface for creating instances of a class, but allow subclasses to alter the type of objects that will be created.\n" +
      "Wireshark - app for network troubleshooting.\n" +
      "Gateway - A computer that sits between different networks or applications. The gateway converts data from one protocol format to another.\n" +
      "hashing - transforming data into a fixed-size code. Deterministic. One hash code can mean multiple original data (collision case).\n" +
      "Gateway team in an enterprise - ensures a secure digital environment by managing network security, access controls, and incident response protocols to safeguard data effectively.\n";

  private String other =
      "NFC - Near Field Communication - wireless communication technology that allows data exchange between devices over a very short distance, typically a few centimeters.\n" +
      "What is a product backlog in agile project management? - prioritized list of features, enhancements, bug fixes, and other work items that need to be addressed in a product.\n" +
      "Stakeholders - everyone with interest in outcome - customers/users, APO, analysts, team, testers, manager, regulatory authorities, security, leadership, marketing, support, customer service etc.\n" +
      "YubiKey - A flash drive for security purposes on a Mac.\n" +
      "WoW - way of working.\n" +
      "CFT - cross functional team - individuals with different areas of expertise.\n" +
      "Value stream - sequence of processes, company uses to deliver product. Encompasses all the steps. May include insights into the efficiency, bottlenecks, areas for improvements.\n" +
      "Life cycle management - managing the entire lifespan of a product. Includes initial concept, development through usage, maintenance and retirement.\n";

  private String spring =
      "Spring boot - part of Spring (Java framework) specific for creating web app back end.\n" +
      "DRY - Don't Repeat Yourself - programming principle.\n" +
      "POJO - Plain Old Java Object - Java class that does not extend or implement any specialized classes or interfaces from frameworks or libraries.\n" +
      "Java beans - simple objects with only getters and setters for the attributes.\n" +
      "Spring beans - POJOs configured in the application context.\n" +
      "State - current values of fields of an object.\n" +
      "IoC - Inversion of Control - design principle - object creation and lifecycle is inverted or delegated to a container or framework, as opposed to being handled by the objects themselves.\n" +
      "Servlet/Controller/MVC - Model View Controller - handles user input, process it, and update the model or view.\n" +
      "Servlet container - handles multithreading and lifecycle of servlets. Examples: Jetty, Apache Tomcat.\n" +
      ".war file - Web Application Archive - packaging format used for distributing and deploying web applications in the Java ecosystem. Similar to .jar file.\n" +
      "Bean factory/ Application context - dependency injection container in Spring.\n" +
      "JDBC - Java Database Connectivity - Java-based API that provides a standard interface for connecting to relational databases and executing SQL queries.\n" +
      "JPA - Java Persistence API - Java specification for managing relational data in Java applications. It provides a standard way to map Java objects to database tables and vice versa.\n" +
      "Hibernate - ORM framework for the Java - forerunner of JPA.\n" +
      "JPQL - Java Persistence Query Language - interacts with databases in a platform-independent way. Automatic translation to actual SQL query depending on platform. Used with JPA.\n" +
      "Web container - servlet container or servlet engine - component of a server that manages the execution of servlets and Java Server Pages (JSP). \n" +
      "JSP - Java Server Pages - allows Java code directly within HTML pages. Provides tags for iteration, conditionals, and formatting.\n" +
      "Liferay - portal and content management system (CMS) written in Java. Provides a platform for building websites and various web apps.\n" +
      "Web portal - platform - provides access to services.\n" +
      "Web - WWW - content over the internet. Including platform and protocols.\n" +
      "Metadata - describes various aspects of data, such as its format, source, purpose, quality, and relationships with other data.\n" +
      "Pointcut - set of criteria that defines a certain point in the execution of a program.\n" +
      "Component scanning - method of configuration: annotations added to classes, attributes, and methods. Helps defining bean.\n" +
      "Proxy pattern - creating placeholders for another object to control access to it, but it doesn't necessarily mean creating copies of the target object.\n" +
      "RDBMS - relational database management system.\n" +
      "DSL - Domain-Specific Language - syntax, designed for a specific problem domain, as opposed to being a general-purpose. Example: Thymeleaf provides DSL for HTML.\n" +
      "Stereotype - role of class, marked by annotation.\n";

  public String getQa() {
    return qa;
  }

  public String getContainerisation() {
    return containerisation;
  }

  public String getApis() {
    return apis;
  }

  public String getLogs() {
    return logs;
  }

  public String getEsealing() {
    return esealing;
  }

  public String getDatabases() {
    return databases;
  }

  public String getRegulations() {
    return regulations;
  }

  public String getSoftwareEngineering() {
    return softwareEngineering;
  }

  public String getOther() {
    return other;
  }

  public String getSpring() {
    return spring;
  }

  public String getAllOneLiners() {
    return qa + containerisation + apis + logs + esealing + databases + regulations + softwareEngineering + other + spring;
  }
}
