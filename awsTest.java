package aws;

/*
 * Cloud Computing
 *
 * Dynamic Resource Management Tool
 * using AWS Java SDK Library
 *
 */
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

public class awsTest {

	static AmazonEC2      ec2;

	private static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("us-east-1")	/* check the region at AWS console */
				.build();
	}

	public static void main(String[] args) throws Exception {

		init();

		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;

		while(true)
		{
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("------------------------------------------------------------");
			System.out.println("           Amazon AWS Control Panel using SDK               ");
			System.out.println("------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones        ");
			System.out.println("  3. start instance               4. available regions      ");
			System.out.println("  5. stop instance                6. create instance        ");
			System.out.println("  7. reboot instance              8. terminate instance     ");
			System.out.println("  9. list images                 10. condor status          ");
			System.out.println(" 11. monitor & Manage Resources  12. create snapshot        ");
			System.out.println(" 13. list snapshots              14. delete snapshot        ");
			System.out.println(" 15. copy snapshot               16. create security group  ");
			System.out.println(" 17. list security groups        18. delete security group  ");
			System.out.println(" 19. add inbound rule            20. remove inbound rule    ");
			System.out.println("                                 99. quit                   ");

			System.out.println("------------------------------------------------------------");

			System.out.print("Enter an integer: ");

			if(menu.hasNextInt()){
				number = menu.nextInt();
			}else {
				System.out.println("concentration!");
				break;
			}


			String instance_id = "";

			switch(number) {
				case 1:
					listInstances();
					break;

				case 2:
					availableZones();
					break;

				case 3:
					System.out.print("Enter instance id: ");
					if(id_string.hasNext())
						instance_id = id_string.nextLine();

					if(!instance_id.trim().isEmpty())
						startInstance(instance_id);
					break;

				case 4:
					availableRegions();
					break;

				case 5:
					System.out.print("Enter instance id: ");
					if(id_string.hasNext())
						instance_id = id_string.nextLine();

					if(!instance_id.trim().isEmpty())
						stopInstance(instance_id);
					break;

				case 6:
					System.out.print("Enter AMI ID: ");
					String amiId = id_string.nextLine();

					System.out.print("Enter instance name: ");
					String instanceName = id_string.nextLine();

					if (!amiId.trim().isEmpty() && !instanceName.trim().isEmpty()) {
						createInstance(amiId, instanceName);
					} else {
						System.out.println("AMI ID and Instance Name cannot be empty.");
					}
					break;


				case 7:
					System.out.print("Enter instance id: ");
					if(id_string.hasNext())
						instance_id = id_string.nextLine();

					if(!instance_id.trim().isEmpty())
						rebootInstance(instance_id);
					break;
				case 8:
					System.out.print("Enter instance ID to terminate: ");
					String instanceIdToTerminate = id_string.nextLine();

					if (!instanceIdToTerminate.trim().isEmpty()) {
						terminateInstance(instanceIdToTerminate);
					} else {
						System.out.println("Instance ID cannot be empty.");
					}
					break;

				case 9:
					listImages();
					break;
				case 10:
					System.out.print("Enter instance id: ");
					if (id_string.hasNext()) {
						instance_id = id_string.nextLine();
					}

					if (!instance_id.trim().isEmpty()) {
						condor_status(instance_id);
					} else {
						System.out.println("Instance ID cannot be empty.");
					}
					break;
				case 11:
					System.out.print("Enter instance id to monitor: ");
					if (id_string.hasNext()) {
						instance_id = id_string.nextLine();
					}

					if (!instance_id.trim().isEmpty()) {
						System.out.print("Enter CPU utilization threshold (e.g., 80 for 80%): ");
						int threshold = 0;
						if (menu.hasNextInt()) {
							threshold = menu.nextInt();
						}

						if (threshold > 0) {
							monitorAndManageResources(instance_id, threshold);
						} else {
							System.out.println("Invalid threshold value.");
						}
					} else {
						System.out.println("Instance ID cannot be empty.");
					}
					break;
				case 12:
					System.out.print("Enter instance id to create snapshot for: ");
					if (id_string.hasNext()) {
						instance_id = id_string.nextLine();
					}

					if (!instance_id.trim().isEmpty()) {
						createSnapshot(instance_id);
					} else {
						System.out.println("Instance ID cannot be empty.");
					}
					break;
				case 13:
					listSnapshots();
					break;
				case 14:
					System.out.print("Enter snapshot ID to delete: ");
					String snapshotId = id_string.nextLine();
					if (!snapshotId.trim().isEmpty()) {
						deleteSnapshot(snapshotId);
					} else {
						System.out.println("Snapshot ID cannot be empty.");
					}
					break;
				case 15:
					System.out.print("Enter source snapshot ID: ");
					String sourceSnapshotId = id_string.nextLine();

					System.out.print("Enter source region: ");
					String sourceRegion = id_string.nextLine();

					System.out.print("Enter destination region: ");
					String destinationRegion = id_string.nextLine();

					if (!sourceSnapshotId.trim().isEmpty() && !sourceRegion.trim().isEmpty() && !destinationRegion.trim().isEmpty()) {
						copySnapshot(sourceSnapshotId, sourceRegion, destinationRegion);
					} else {
						System.out.println("All fields are required.");
					}
					break;
				case 16:
					System.out.print("Enter security group name: ");
					String groupName = id_string.nextLine();
					System.out.print("Enter description: ");
					String description = id_string.nextLine();
					createSecurityGroup(groupName, description);
					break;

				case 17:
					listSecurityGroups();
					break;

				case 18:
					System.out.print("Enter security group ID to delete: ");
					String groupIdToDelete = id_string.nextLine();
					deleteSecurityGroup(groupIdToDelete);
					break;

				case 19:
					System.out.print("Enter security group ID: ");
					String groupIdForRule = id_string.nextLine();
					System.out.print("Enter protocol (e.g., tcp): ");
					String protocol = id_string.nextLine();
					System.out.print("Enter port: ");
					int port = menu.nextInt();
					menu.nextLine(); // consume newline
					System.out.print("Enter CIDR (e.g., 0.0.0.0/0): ");
					String cidr = id_string.nextLine();
					addInboundRule(groupIdForRule, protocol, port, cidr);
					break;

				case 20:
					System.out.print("Enter security group ID: ");
					String groupIdToRemoveRule = id_string.nextLine();
					System.out.print("Enter protocol (e.g., tcp): ");
					String protocolToRemove = id_string.nextLine();
					System.out.print("Enter port: ");
					int portToRemove = menu.nextInt();
					menu.nextLine(); // consume newline
					System.out.print("Enter CIDR (e.g., 0.0.0.0/0): ");
					String cidrToRemove = id_string.nextLine();
					removeInboundRule(groupIdToRemoveRule, protocolToRemove, portToRemove, cidrToRemove);
					break;



				case 99:
					System.out.println("bye!");
					menu.close();
					id_string.close();
					return;
				default: System.out.println("concentration!");
			}

		}

	}

	private static void handleAmazonException(AmazonServiceException e) {
		System.out.printf("Error: %s, Status Code: %d, AWS Error Code: %s, Request ID: %s\n",
				e.getMessage(), e.getStatusCode(), e.getErrorCode(), e.getRequestId());
	}

	private static void handleException(Exception e) {
		System.out.printf("Exception: %s\n", e.getMessage());
	}


	public static void listInstances() {

		System.out.println("Listing instances....");
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();

		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					// 태그에서 Name 값 추출
					String name = "N/A";
					if (instance.getTags() != null) {
						for (Tag tag : instance.getTags()) {
							if ("Name".equals(tag.getKey())) {
								name = tag.getValue();
								break;
							}
						}
					}

					// Name 태그를 가장 앞에 출력
					System.out.printf(
							"[Name] %s, " +
									"[id] %s, " +
									"[AMI] %s, " +
									"[type] %s, " +
									"[state] %10s, " +
									"[monitoring state] %s\n",
							name,
							instance.getInstanceId(),
							instance.getImageId(),
							instance.getInstanceType(),
							instance.getState().getName(),
							instance.getMonitoring().getState()
					);
				}
			}

			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}


	public static void availableZones()	{

		System.out.println("Available zones....");
		try {
			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			Iterator <AvailabilityZone> iterator = availabilityZonesResult.getAvailabilityZones().iterator();

			AvailabilityZone zone;
			while(iterator.hasNext()) {
				zone = iterator.next();
				System.out.printf("[id] %s,  [region] %15s, [zone] %15s\n", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
			}
			System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
					" Availability Zones.");

		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}

	}

	public static void startInstance(String instance_id)
	{

		System.out.printf("Starting .... %s\n", instance_id);
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StartInstancesRequest> dry_request =
				() -> {
					StartInstancesRequest request = new StartInstancesRequest()
							.withInstanceIds(instance_id);

					return request.getDryRunRequest();
				};

		StartInstancesRequest request = new StartInstancesRequest()
				.withInstanceIds(instance_id);

		ec2.startInstances(request);

		System.out.printf("Successfully started instance %s", instance_id);
	}


	public static void availableRegions() {

		System.out.println("Available regions ....");

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
			System.out.printf(
					"[region] %15s, " +
							"[endpoint] %s\n",
					region.getRegionName(),
					region.getEndpoint());
		}
	}

	public static void stopInstance(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DryRunSupportedRequest<StopInstancesRequest> dry_request =
				() -> {
					StopInstancesRequest request = new StopInstancesRequest()
							.withInstanceIds(instance_id);

					return request.getDryRunRequest();
				};

		try {
			StopInstancesRequest request = new StopInstancesRequest()
					.withInstanceIds(instance_id);

			ec2.stopInstances(request);
			System.out.printf("Successfully stop instance %s\n", instance_id);

		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}

	}

	public static void createInstance(String amiId, String instanceName) {
		System.out.printf("Creating instance with AMI ID %s and Name %s...\n", amiId, instanceName);

		try {
			// 인스턴스 생성 요청
			RunInstancesRequest runRequest = new RunInstancesRequest()
					.withImageId(amiId)
					.withInstanceType(InstanceType.T2Micro) // 인스턴스 타입 설정
					.withMinCount(1)
					.withMaxCount(1);

			RunInstancesResult runResponse = ec2.runInstances(runRequest);

			// 생성된 인스턴스 ID 가져오기
			String instanceId = runResponse.getReservation().getInstances().get(0).getInstanceId();
			System.out.printf("Instance created with ID: %s\n", instanceId);

			// 태그 추가 요청
			CreateTagsRequest tagRequest = new CreateTagsRequest()
					.withResources(instanceId)
					.withTags(new Tag().withKey("Name").withValue(instanceName));

			ec2.createTags(tagRequest);

			System.out.printf("Instance %s tagged with Name: %s\n", instanceId, instanceName);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}


	public static void rebootInstance(String instance_id) {

		System.out.printf("Rebooting .... %s\n", instance_id);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		try {
			RebootInstancesRequest request = new RebootInstancesRequest()
					.withInstanceIds(instance_id);

			RebootInstancesResult response = ec2.rebootInstances(request);

			System.out.printf(
					"Successfully rebooted instance %s", instance_id);

		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}


	}

	public static void terminateInstance(String instanceId) {
		System.out.printf("Terminating instance %s...\n", instanceId);

		try {
			TerminateInstancesRequest request = new TerminateInstancesRequest()
					.withInstanceIds(instanceId);

			TerminateInstancesResult result = ec2.terminateInstances(request);

			for (InstanceStateChange stateChange : result.getTerminatingInstances()) {
				System.out.printf("Instance ID: %s, Previous State: %s, Current State: %s\n",
						stateChange.getInstanceId(),
						stateChange.getPreviousState().getName(),
						stateChange.getCurrentState().getName());
			}
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}


	public static void listImages() {
		System.out.println("Listing images....");

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		DescribeImagesRequest request = new DescribeImagesRequest();
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

		request.getFilters().add(new Filter().withName("name").withValues("aws-htcondor-slave"));
		request.setRequestCredentialsProvider(credentialsProvider);

		DescribeImagesResult results = ec2.describeImages(request);

		for(Image images :results.getImages()){
			System.out.printf("[ImageID] %s, [Name] %s, [Owner] %s\n",
					images.getImageId(), images.getName(), images.getOwnerId());
		}

	}

	public static void condor_status(String instance_id) {
		System.out.printf("Checking HTCondor status for instance %s...\n", instance_id);
		try {
			// 1. instance_id로 EC2 퍼블릭 IP 주소 가져오기
			String publicIp = getPublicIp(instance_id);
			if (publicIp == null || publicIp.isEmpty()) {
				System.out.println("Could not retrieve the public IP for the instance.");
				return;
			}

			// 2. SSH 명령어 구성
			String privateKeyPath = "/home/cloud/cloud-test.pem"; // PEM 파일 경로
			String user = "ec2-user";
			String command = String.format("ssh -i %s %s@%s condor_status", privateKeyPath, user, publicIp);

			// 3. ProcessBuilder를 사용해 명령어 실행
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
			Process process = processBuilder.start();

			// 4. 프로세스 출력 읽기
			Scanner scanner = new Scanner(process.getInputStream());
			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}

			// 5. 프로세스 종료 코드 확인
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.printf("HTCondor status for instance %s checked successfully.\n", instance_id);
			} else {
				System.out.printf("Error while checking HTCondor status for instance %s. Exit code: %d\n", instance_id, exitCode);
			}
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private static String getPublicIp(String instance_id) {
		try {
			// EC2 인스턴스 정보 요청
			DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instance_id);
			DescribeInstancesResult response = ec2.describeInstances(request);

			// 퍼블릭 IP 주소 추출
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					if (instance.getInstanceId().equals(instance_id)) {
						return instance.getPublicIpAddress();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error retrieving public IP: " + e.getMessage());
		}
		return null; // 퍼블릭 IP가 없거나 요청 실패 시 null 반환
	}

	public static void monitorAndManageResources(String instance_id, int threshold) {
		System.out.printf("Monitoring CPU usage for instance %s with threshold %d%%...\n", instance_id, threshold);

		try {
			// AWS CloudWatch 클라이언트 초기화
			AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder.standard().build();

			// CloudWatch에서 CPU 사용률 지표 요청
			GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
					.withNamespace("AWS/EC2")
					.withMetricName("CPUUtilization")
					.withDimensions(new Dimension().withName("InstanceId").withValue(instance_id))
					.withStatistics("Average")
					.withPeriod(300) // 5분 간격
					.withStartTime(Date.from(Instant.now().minus(Duration.ofMinutes(10))))
					.withEndTime(Date.from(Instant.now()));

			GetMetricStatisticsResult response = cloudWatch.getMetricStatistics(request);

			if (response.getDatapoints().isEmpty()) {
				System.out.println("No data available for CPU usage.");
				return;
			}

			// 가장 최근의 CPU 사용률 확인
			Datapoint latestData = response.getDatapoints().stream()
					.max(Comparator.comparing(Datapoint::getTimestamp))
					.orElseThrow(() -> new RuntimeException("No valid datapoints found."));

			double cpuUtilization = latestData.getAverage();
			System.out.printf("Current CPU utilization: %.2f%%\n", cpuUtilization);

			// CPU 사용률이 임계값 초과 시 정지
			if (cpuUtilization > threshold) {
				System.out.printf("CPU utilization %.2f%% exceeds threshold of %d%%. Taking action...\n", cpuUtilization, threshold);
				stopInstance(instance_id);
			} else {
				System.out.printf("CPU utilization %.2f%% is within acceptable range.\n", cpuUtilization);
			}

		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void createSnapshot(String instance_id) {
		System.out.printf("Creating snapshot for instance %s...\n", instance_id);

		try {
			// 인스턴스의 볼륨 ID 가져오기
			DescribeInstancesRequest describeRequest = new DescribeInstancesRequest().withInstanceIds(instance_id);
			DescribeInstancesResult describeResult = ec2.describeInstances(describeRequest);

			String volumeId = null;
			for (Reservation reservation : describeResult.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					if (!instance.getBlockDeviceMappings().isEmpty()) {
						volumeId = instance.getBlockDeviceMappings().get(0).getEbs().getVolumeId();
						break;
					}
				}
			}

			if (volumeId == null) {
				System.out.println("No volume attached to the instance.");
				return;
			}

			// 스냅샷 생성 요청
			CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest()
					.withVolumeId(volumeId)
					.withDescription("Snapshot for instance " + instance_id);
			CreateSnapshotResult snapshotResult = ec2.createSnapshot(snapshotRequest);

			// 스냅샷 결과 출력
			System.out.printf("Snapshot created with ID: %s\n", snapshotResult.getSnapshot().getSnapshotId());
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}

	public static void listSnapshots() {
		System.out.println("Fetching list of snapshots...");
		try {
			// 스냅샷 목록 조회 요청
			DescribeSnapshotsRequest request = new DescribeSnapshotsRequest().withOwnerIds("self"); // 자신의 스냅샷만 조회
			DescribeSnapshotsResult response = ec2.describeSnapshots(request);

			// 스냅샷 정보 출력
			for (Snapshot snapshot : response.getSnapshots()) {
				System.out.printf(
						"Snapshot ID: %s, Volume ID: %s, State: %s, Start Time: %s, Description: %s\n",
						snapshot.getSnapshotId(),
						snapshot.getVolumeId(),
						snapshot.getState(),
						snapshot.getStartTime(),
						snapshot.getDescription()
				);
			}
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}


	public static void deleteSnapshot(String snapshotId) {
		System.out.printf("Deleting snapshot %s...\n", snapshotId);
		try {
			// 스냅샷 삭제 요청
			DeleteSnapshotRequest request = new DeleteSnapshotRequest().withSnapshotId(snapshotId);
			ec2.deleteSnapshot(request);

			System.out.printf("Snapshot %s successfully deleted.\n", snapshotId);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}

	public static void copySnapshot(String sourceSnapshotId, String sourceRegion, String destinationRegion) {
		System.out.printf("Copying snapshot %s from %s to %s...\n", sourceSnapshotId, sourceRegion, destinationRegion);

		try {
			// 스냅샷 복사 요청 생성
			CopySnapshotRequest copyRequest = new CopySnapshotRequest()
					.withSourceSnapshotId(sourceSnapshotId)
					.withSourceRegion(sourceRegion)
					.withDestinationRegion(destinationRegion)
					.withDescription("Snapshot copy from " + sourceRegion + " to " + destinationRegion);

			// 스냅샷 복사 실행
			CopySnapshotResult copyResult = ec2.copySnapshot(copyRequest);

			// 결과 출력
			String newSnapshotId = copyResult.getSnapshotId();
			System.out.printf("Snapshot successfully copied. New snapshot ID: %s\n", newSnapshotId);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void createSecurityGroup(String groupName, String description) {
		System.out.printf("Creating security group %s...\n", groupName);
		try {
			CreateSecurityGroupRequest request = new CreateSecurityGroupRequest()
					.withGroupName(groupName)
					.withDescription(description);
			CreateSecurityGroupResult result = ec2.createSecurityGroup(request);

			System.out.printf("Security group created with ID: %s\n", result.getGroupId());
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void listSecurityGroups() {
		System.out.println("Listing security groups...");
		try {
			DescribeSecurityGroupsResult result = ec2.describeSecurityGroups();

			for (SecurityGroup group : result.getSecurityGroups()) {
				System.out.printf("Group Name: %s, Group ID: %s, Description: %s, VPC ID: %s\n",
						group.getGroupName(),
						group.getGroupId(),
						group.getDescription(),
						group.getVpcId());
			}
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void deleteSecurityGroup(String groupId) {
		System.out.printf("Deleting security group %s...\n", groupId);
		try {
			DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
					.withGroupId(groupId);
			ec2.deleteSecurityGroup(request);

			System.out.printf("Security group %s successfully deleted.\n", groupId);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void addInboundRule(String groupId, String protocol, int port, String cidr) {
		System.out.printf("Adding inbound rule to security group %s...\n", groupId);
		try {
			IpPermission ipPermission = new IpPermission()
					.withIpProtocol(protocol)
					.withFromPort(port)
					.withToPort(port)
					.withIpv4Ranges(new IpRange().withCidrIp(cidr));


			AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest()
					.withGroupId(groupId)
					.withIpPermissions(ipPermission);

			ec2.authorizeSecurityGroupIngress(request);
			System.out.printf("Inbound rule added: protocol=%s, port=%d, cidr=%s\n", protocol, port, cidr);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static void removeInboundRule(String groupId, String protocol, int port, String cidr) {
		System.out.printf("Removing inbound rule from security group %s...\n", groupId);
		try {
			IpPermission ipPermission = new IpPermission()
					.withIpProtocol(protocol)
					.withFromPort(port)
					.withToPort(port)
					.withIpv4Ranges(new IpRange().withCidrIp(cidr));

			RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest()
					.withGroupId(groupId)
					.withIpPermissions(ipPermission);

			ec2.revokeSecurityGroupIngress(request);
			System.out.printf("Inbound rule removed: protocol=%s, port=%d, cidr=%s\n", protocol, port, cidr);
		} catch (AmazonServiceException e) {
			handleAmazonException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}
}
	