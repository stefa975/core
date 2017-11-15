/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.core;

import com.google.gwt.i18n.client.Constants;


/**
 * @author Heiko Braun
 * @author David Bosschaert
 * @date 5/2/11
 */
public interface UIConstants extends Constants {
    // @formatter:off
    String accessDenied();
    String addressCriteriaIsValid();
    String addressingDescription();
    String administration_add_scoped_role();
    String administration_assignment_realm_desc();
    String administration_assignment_roles_desc();
    String administration_assignment_user_group_desc();
    String administration_audit_log_desc();
    String administration_members();
    String administration_role_include_all_desc();
    String administration_scoped_role_base_role_desc();
    String administration_scoped_role_scope_desc();
    String allContent();
    String allDeploymentsAlreadyAssigned();
    String allSingletonsAlreadyAdded();
    String anyAddressModified();
    String anyChanges();
    String anyLoopbackModified();
    String anyNicModified();
    String anythingConflictsWithAddressWildcard();
    String anythingConflictsWithInetAddress();
    String assignContent();
    String asyncCallFailed();
    String attributeDescriptionsNotAvailable();
    String attributesNotSupported();
    String availableChildrenTypes();
    String batchSubsystemDescription();
    String bridgeDescription();
    String broadcastDescription();
    String browseBy();
    String bs_configure_interface_desc();
    String bs_configure_interface_duplicate();
    String bs_configure_interface_header();
    String bs_configure_interface_name_placeholder();
    String bs_connect_interface_connect();
    String bs_connect_interface_desc();
    String bs_connect_interface_header();
    String bs_connect_interface_no_selection();
    String bs_interface_success();
    String bs_ping();
    String cannotCreateChildResource();
    String cannotLoadSubsystems();
    String cannotReadDeploymentScanner();
    String cannotUploadDeployment();
    String chooseDeploymentScanner();
    String chooseFromContentRepository();
    String chooseFromContentRepositoryDescription();
    String clusterConnectionDescription();
    String commom_label_filter();
    String common_error_unknownError();
    String common_label_add();
    String common_label_addContent();
    String common_label_addItem();
    String common_label_addProperty();
    String common_label_advanced();
    String common_label_append();
    String common_label_areYouSure();
    String common_label_assign();
    String common_label_attributes();
    String common_label_autoStart();
    String common_label_browseContent();
    String common_label_cancel();
    String common_label_chooseFile();
    String common_label_clear();
    String common_label_clone();
    String common_label_continue();
    String common_label_copy();
    String common_label_date();
    String common_label_delete();
    String common_label_deployments();
    String common_label_details();
    String common_label_disable();
    String common_label_done();
    String common_label_explode();
    String common_label_edit();
    String common_label_enable();
    String common_label_enOrDisable();
    String common_label_exclude();
    String common_label_export();
    String common_label_filter();
    String common_label_finish();
    String common_label_generalConfig();
    String common_label_group();
    String common_label_groups();
    String common_label_host();
    String common_label_hostConfiguration();
    String common_label_include();
    String common_label_includes();
    String common_label_interfaces();
    String common_label_item();
    String common_label_key();
    String common_label_logout();
    String common_label_manageDeployments();
    String common_label_messageDetailTitle();
    String common_label_messages();
    String common_label_name();
    String common_label_next();
    String common_label_noRecentMessages();
    String common_label_operations();
    String common_label_option();
    String common_label_patch_stream();
    String common_label_paths();
    String common_label_plaseWait();
    String common_label_portOffset();
    String common_label_priority();
    String common_label_predicate();
    String common_label_probe();
    String common_label_profile();
    String common_label_properties();
    String common_label_recover();
    String common_label_refresh();
    String common_label_reload();
    String common_label_remove();
    String common_label_replace();
    String common_label_requestProcessed();
    String common_label_reset();
    String common_label_restart();
    String common_label_role();
    String common_label_roles();
    String common_label_runtimeName();
    String common_label_save();
    String common_label_search();
    String common_label_selection();
    String common_label_serverConfigs();
    String common_label_serverGroupConfigurations();
    String common_label_serverGroups();
    String common_label_setSecret();
    String common_label_settings();
    String common_label_socketBinding();
    String common_label_start();
    String common_label_stats();
    String common_label_stop();
    String common_label_subsystems();
    String common_label_success();
    String common_label_systemProperties();
    String common_label_type();
    String common_label_unassign();
    String common_label_user();
    String common_label_users();
    String common_label_value();
    String common_label_view();
    String common_label_virtualMachine();
    String common_label_virtualMachines();
    String common_serverConfig_desc();
    String common_serverGroups_desc();
    String common_socket_bindings_desc();
    String connectedTo();
    String connecto_to_desc_domain();
    String connecto_to_desc_standalone();
    String containerSettings();
    String copyServerDescription();
    String createProfile();
    String createUnmanaged();
    String createUnmanagedDescription();
    String deploymentEnabledDescription();
    String deploymentNameDescription();
    String deploymentRuntimeNameDescription();
    String deploymentCannotReadUnmanaged();
    String deploymentCannotReadUnexploded();
    String deploymentCannotReplaceUnmanaged();
    String deploymentCannotExplodeUnmanaged();
    String deploymentCannotExplodeExploded();
    String deploymentCannotExplodeEnabled();
    String discoveryGroupDescription();
    String discoveryGroupOrConnectorsCanBeDefined();
    String discoveryGroupOrConnectorsMustBeDefined();
    String dismiss();
    String divertDescription();
    String downloadingLogFileConfirmation();
    String downloadInProgress();
    String downloadLogFile();
    String duplicate_data_source_jndi();
    String duplicate_data_source_name();
    String duplicate_mail_server_type();
    String enableAssignmentOnSelectedServerGroups();
    String excludedFrom();
    String expressionsRunningServer();
    String failedToAddServer();
    String failedToCreateSecurityContext();
    String failedToLoadServerNames();
    String failedToParseDescription();
    String failedToReadMetricDescription();
    String failedToRemoveServer();
    String failedToResolveExpression();
    String failedToRetrieveAttributeDescriptions();
    String goToRuntime();
    String groupAlreadyExists();
    String help_close_help();
    String help_need_help();
    String help();
    String homepage_access_control_section();
    String homepage_access_control_step_intro();
    String homepage_access_control_step1();
    String homepage_access_control_step2();
    String homepage_access_control_sub_header();
    String homepage_configuration_domain_step1();
    String homepage_configuration_domain_sub_header();
    String homepage_configuration_section();
    String homepage_configuration_standalone_step1();
    String homepage_configuration_standalone_sub_header();
    String homepage_configuration_step_intro();
    String homepage_configuration_step2();
    String homepage_configuration_step3();
    String homepage_deployments_domain_step_1();
    String homepage_deployments_domain_step_2();
    String homepage_deployments_domain_step_intro();
    String homepage_deployments_section();
    String homepage_deployments_standalone_step_1();
    String homepage_deployments_standalone_step_intro();
    String homepage_deployments_step_enable();
    String homepage_deployments_sub_header();
    String homepage_help_admin_guide_text();
    String homepage_help_consulting_link();
    String homepage_help_consulting_text();
    String homepage_help_developers_mailing_list_text();
    String homepage_help_eap_community_link();
    String homepage_help_eap_community_text();
    String homepage_help_eap_configurations_link();
    String homepage_help_eap_configurations_text();
    String homepage_help_eap_documentation_link();
    String homepage_help_eap_documentation_text();
    String homepage_help_general_resources();
    String homepage_help_get_help();
    String homepage_help_irc_text();
    String homepage_help_knowledgebase_link();
    String homepage_help_knowledgebase_text();
    String homepage_help_latest_news();
    String homepage_help_learn_more_eap_link();
    String homepage_help_learn_more_eap_text();
    String homepage_help_model_reference_text();
    String homepage_help_need_help();
    String homepage_help_training_link();
    String homepage_help_training_text();
    String homepage_help_trouble_ticket_link();
    String homepage_help_trouble_ticket_text();
    String homepage_help_tutorials_link();
    String homepage_help_tutorials_text();
    String homepage_help_user_forums_text();
    String homepage_help_wildfly_issues_text();
    String homepage_help_wilfdfly_documentation_text();
    String homepage_help_wilfdfly_home_text();
    String homepage_jms_domain_step1();
    String homepage_jms_section();
    String homepage_jms_standalone_step1();
    String homepage_jms_step_intro();
    String homepage_jms_step2();
    String homepage_jms_step3();
    String homepage_new_to_eap();
    String homepage_patching_domain_step2();
    String homepage_patching_section();
    String homepage_patching_step_apply();
    String homepage_patching_step1();
    String homepage_runtime_domain_create_server_section();
    String homepage_runtime_domain_create_server_step_intro();
    String homepage_runtime_domain_create_server_step1();
    String homepage_runtime_domain_create_server_step2();
    String homepage_runtime_domain_monitor_server_section();
    String homepage_runtime_domain_monitor_server_step1();
    String homepage_runtime_domain_monitor_server_step2();
    String homepage_runtime_domain_server_group_section();
    String homepage_runtime_domain_server_group_step_intro();
    String homepage_runtime_domain_server_group_step1();
    String homepage_runtime_domain_server_group_step2();
    String homepage_runtime_domain_sub_header();
    String homepage_runtime_standalone_section();
    String homepage_runtime_standalone_step1();
    String homepage_runtime_standalone_step2();
    String homepage_runtime_standalone_sub_header();
    String homepage_runtime_step_intro();
    String homepage_take_a_tour();
    String hornetq_acceptor_type_desc();
    String host_interfaces_desc();
    String host_properties_desc();
    String hosts_jvm_desc();
    String hosts_jvm_err_deleteDefault();
    String hosts_jvm_title();
    String identityProviderDescription();
    String includedFromProfile();
    String insufficientPrivileges();
    String interfaces_desc();
    String interfaces_err_inetAddress_set();
    String interfaces_err_loopback_address_set();
    String interfaces_err_loopback_nor_address_set();
    String interfaces_err_loopback_set();
    String interfaces_err_nic_set();
    String interfaces_err_nicmatch_set();
    String interfaces_err_not_set();
    String interfaces_err_wildcard_nor_address_set();
    String interfaces_err_wildcard_set();
    String interfacesDescription();
    String invalidAddressCriteria();
    String invalidLoopbackCriteria();
    String invalidNicCriteria();
    String invalidOtherCriteria();
    String isAddressWildcardSet();
    String isInetAddressSet();
    String jmsConnectionFactoryDescription();
    String jmsConnectorDescription();
    String jmsConnectorServiceDescription();
    String jndiFormatError();
    String lastActionError();
    String localhostDoesNotWorkReliably();
    String localSocketDescription();
    String logFileDescription();
    String logFilesOfSelectedServer();
    String logout_confirm();
    String loopbackCriteriaIsValid();
    String lostConnection();
    String member();
    String modifyCase();
    String newCacheConfiguration();
    String nicCriteriaIsValid();
    String noChanges();
    String noConfigurableAttributes();
    String noDriverSpecified();
    String noPreselection();
    String noReason();
    String noReferenceServerFound();
    String noUploadDueToSecurityReasons();
    String openCase();
    String otherConstraintsModified();
    String otherCriteriaIsValid();
    String patch_manager_applied_at();
    String patch_manager_applied_success();
    String patch_manager_apply_error_body();
    String patch_manager_apply_error_cancel_body();
    String patch_manager_apply_error_cancel_title();
    String patch_manager_apply_error_select_body();
    String patch_manager_apply_error_select_title();
    String patch_manager_apply_new_wizard_error();
    String patch_manager_apply_new();
    String patch_manager_apply_patch();
    String patch_manager_conflict_body();
    String patch_manager_conflict_cancel_body();
    String patch_manager_conflict_cancel_title();
    String patch_manager_conflict_override_body();
    String patch_manager_conflict_override_check();
    String patch_manager_conflict_title();
    String patch_manager_desc_community();
    String patch_manager_error_title();
    String patch_manager_error();
    String patch_manager_hide_details();
    String patch_manager_in_effect();
    String patch_manager_latest();
    String patch_manager_patch_details();
    String patch_manager_possible_actions();
    String patch_manager_recently();
    String patch_manager_restart_error();
    String patch_manager_restart_no();
    String patch_manager_restart_now();
    String patch_manager_restart_pending();
    String patch_manager_restart_required();
    String patch_manager_restart_timeout();
    String patch_manager_restart_verify();
    String patch_manager_rollback_confirm_body();
    String patch_manager_rollback_confirm_title();
    String patch_manager_rollback_error_body();
    String patch_manager_rollback_error_cancel_body();
    String patch_manager_rollback_error_cancel_title();
    String patch_manager_rollback_error_select_body();
    String patch_manager_rollback_error_select_title();
    String patch_manager_rollback_options_body();
    String patch_manager_rollback_options_override_all_desc();
    String patch_manager_rollback_options_override_all();
    String patch_manager_rollback_options_reset_configuration_desc();
    String patch_manager_rollback_options_reset_configuration();
    String patch_manager_rollback_options_title();
    String patch_manager_rollback_wizard_error();
    String patch_manager_rollback();
    String patch_manager_rolled_back_success_body();
    String patch_manager_rolled_back_success_title();
    String patch_manager_select_file();
    String patch_manager_select_patch_body();
    String patch_manager_select_patch_title();
    String patch_manager_select_patch_upload();
    String patch_manager_servers_shutdown();
    String patch_manager_servers_still_running_warning();
    String patch_manager_show_details();
    String patch_manager_stop_server_error_cancel_body();
    String patch_manager_stop_server_error_cancel_title();
    String patch_manager_stop_server_error_continue_body();
    String patch_manager_stop_server_error_continue_title();
    String patch_manager_stop_server_error();
    String patch_manager_stop_server_no();
    String patch_manager_stop_server_question_for_apply();
    String patch_manager_stop_server_question_for_rollback();
    String patch_manager_stop_server_timeout();
    String patch_manager_stop_server_title();
    String patch_manager_stop_server_unknown_error();
    String patch_manager_stop_server_yes();
    String patch_manager_stopping_servers_body();
    String patch_manager_toolstrip_desc();
    String patch_manager_update();
    String pathsDescription();
    String pleaseChoose();
    String pleaseChooseAnEntry();
    String pleaseChooseFile();
    String pleaseChooseWorker();
    String pleaseChoseBufferPool();
    String pleaseChoseHost();
    String pleaseChoseMessagingProvider();
    String pleaseSelectPrincipal();
    String pleaseSelectRole();
    String pleaseSelectServerGroup();
    String portDescription();
    String properties_global_desc();
    String reason();
    String reloadServerGroup();
    String reloadServerNow();
    String reloadHost();
    String remoteSocketDescription();
    String removeProfile();
    String replace_me();
    String replaceDeployment();
    String requestTimeout();
    String resolve();
    String resolvedValue();
    String resolveExpressionValues();
    String resource_already_exists();
    String restartServerGroup();
    String restartHost();
    String resume();
    String resumeServerGroup();
    String runAs();
    String runAsRole();
    String search_index_reset();
    String search_no_results();
    String search_placeholder();
    String search_popup_title();
    String search_tooltip_osx();
    String search_tooltip_other();
    String securitySettingsDescription();
    String selectResourceType();
    String selectRole();
    String selectRoleDescription();
    String server_config_desc();
    String server_config_uptodate();
    String server_instance_pleaseSelect();
    String server_instance_reloadRequired();
    String server_instance_servers_needReload();
    String server_instance_servers_needReload_from_runtime();
    String server_instance_servers_needRestart();
    String server_reload_desc();
    String server_reload_title();
    String serverConfigurationChanged();
    String serverConfigurationNeedsToBeReloaded();
    String serverDescription();
    String serverNeedsToBeRestarted();
    String serviceProviderDescription();
    String servletContainerDescription();
    String socketBindingDescription();
    String standardRolesCannotBeRemoved();
    String startServerGroup();
    String statisticsEnabledError();
    String stopServerGroup();
    String sso_access_control_description();
    String sso_access_control_service_title();
    String sso_access_control_user_profile();
    String subsys_configadmin_add();
    String subsys_configadmin_editPID();
    String subsys_configadmin_header();
    String subsys_configadmin_PID();
    String subsys_configadmin_PIDLabel();
    String subsys_configadmin_PIDShort();
    String subsys_configadmin_valueAdd();
    String subsys_configadmin_valuesLabel();
    String subsys_configadmin();
    String subsys_ee_desc();
    String subsys_elytron_ldap_keystore_newattribute_desc();
    String subsys_iiop_openjdk_desc();
    String subsys_jca_boostrap_config_desc();
    String subsys_jca_common_config_desc();
    String subsys_jca_dataSource_choose_template();
    String subsys_jca_dataSource_custom_template();
    String subsys_jca_datasource_error_load();
    String subsys_jca_dataSource_metric_desc();
    String subsys_jca_dataSource_step1();
    String subsys_jca_dataSource_step2();
    String subsys_jca_dataSource_step3();
    String subsys_jca_dataSource_summary();
    String subsys_jca_dataSource_verify();
    String subsys_jca_dataSource_xaprop_help();
    String subsys_jca_dataSources_desc();
    String subsys_jca_dataSources();
    String subsys_jca_dataSourcesXA();
    String subsys_jca_pool_statistics_tab();
    String subsys_jca_err_prop_required();
    String subsys_jca_error_context_removal_desc();
    String subsys_jca_error_context_removal();
    String subsys_jca_error_datasource_notenabled();
    String subsys_jca_error_default_workmanager_deletion();
    String subsys_jca_error_pool_exist_desc();
    String subsys_jca_error_pool_exist();
    String subsys_jca_error_pool_removal_desc();
    String subsys_jca_error_pool_removal();
    String subsys_jca_threadpool_config_desc();
    String subsys_jca_workmanager_config_desc();
    String subsys_jca_xadataSource_step1();
    String subsys_jca_xadataSource_step2();
    String subsys_jca_xadataSource_step3();
    String subsys_jca_xadataSource_step4();
    String subsys_jca_xadataSources_desc();
    String subsys_jgroups_err_protocols_required();
    String subsys_jgroups_protocol_desc();
    String subsys_jgroups_session_desc();
    String subsys_jgroups_step1();
    String subsys_jgroups_step2();
    String subsys_jgroups_transport_desc();
    String subsys_jmx_desc();
    String subsys_jpa_basicMetric_desc();
    String subsys_jpa_deployment_desc();
    String subsys_jpa_desc();
    String subsys_jpa_puList_desc();
    String subsys_mail_server_desc();
    String subsys_mail_session_desc();
    String subsys_messaging_queue_metric_desc();
    String subsys_messaging_topic_metric_desc();
    String subsys_messaging_pooled_stats_desc();
    String subsys_modcluster_desc();
    String subsys_naming_jndiBindings();
    String subsys_naming_jndiView();
    String subsys_naming_selectedURI();
    String subsys_naming_URI();
    String subsys_naming_type();
    String subsys_naming_value();
    String subsys_web_desc();
    String subsys_web_socketInUse();
    String subsys_ws_desc();
    String subsys_ws_endpoint();
    String subsys_ws_endpoint_desc();
    String subsys_ws_provider();
    String subsys_ws_remove_handler();
    String subsys_ws_wise_title_description();
    String subsystemsDescription();
    String subys_tx_desc();
    String subys_tx_metric_desc();
    String subys_web_metric_desc();
    String successfullyRefreshedStatistics();
    String suspend();
    String suspendTimeoutDescription();
    String stopTimeoutDescription();
    String systemPropertiesDescription();
    String transportSettings();
    String unableToAddDeployment();
    String unableToAddUnmanagedDeployment();
    String unableToAssignDeployment();
    String unableToFindDeployments();
    String unableToLoadConsole();
    String unableToLoadDeployments();
    String unableToModifyDeployment();
    String unableToNavigateBack();
    String unableToRemoveDeployment();
    String unableToExplodeDeployment();
    String unableToReadDeployment();
    String unableToResolve();
    String unassign();
    String unassigned();
    String unassignedContent();
    String unauthorized_desc();
    String unauthorized();
    String unauthorizedAdd();
    String unauthorizedRemove();
    String undertowDescription();
    String undertowApplicationSecurityDomain();
    String unmanagedDeploymentArchiveDescription();
    String unmanagedDeploymentPathDescription();
    String unmanagedDeploymentRelativeToDescription();
    String uploadedDeployments();
    String uploadNewDeployment();
    String uploadNewDeploymentDescription();
    String uploadsNotSupported();
    String userAlreadyExists();
    String validAddressCriteria();
    String validAddressWildcard();
    String validInetAddress();
    String validLoopbackCriteria();
    String validNicConstraints();
    String validOtherConstraints();
    String verify_datasource_dependent_error();
    String verify_datasource_disabled();
    String verify_datasource_failed_header();
    String verify_datasource_internal_error();
    String verify_datasource_no_running_servers();
    String verify_datasource_successful_header();
    String verifyUpload();
    String wizard_back();
}
