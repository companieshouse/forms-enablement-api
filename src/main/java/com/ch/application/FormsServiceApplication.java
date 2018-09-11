package com.ch.application;

import static com.ch.service.LoggingService.LoggingLevel.INFO;
import static com.ch.service.LoggingService.tag;

import com.ch.auth.FormsApiAuthenticator;
import com.ch.client.PresenterHelper;
import com.ch.client.SalesforceClientHelper;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.configuration.SalesforceConfiguration;
import com.ch.exception.mapper.ConnectionExceptionMapper;
import com.ch.exception.mapper.ContentTypeExceptionMapper;
import com.ch.exception.mapper.DatabaseExceptionMapper;
import com.ch.exception.mapper.MissingRequiredDataExceptionMapper;
import com.ch.exception.mapper.PackageContentsExceptionMapper;
import com.ch.exception.mapper.PresenterAuthenticationExceptionMapper;
import com.ch.exception.mapper.XmlExceptionMapper;
import com.ch.exception.mapper.XsdValidationExceptionMapper;
import com.ch.filters.LoggingFilter;
import com.ch.filters.PreventAccessFilter;
import com.ch.filters.RateLimitFilter;
import com.ch.health.AppHealthCheck;
import com.ch.health.MongoHealthCheck;
import com.ch.helpers.ClientHelper;
import com.ch.helpers.MongoHelper;
import com.ch.model.FormsApiUser;
import com.ch.resources.BarcodeResource;
import com.ch.resources.ConfirmPaymentResource;
import com.ch.resources.FormResponseResource;
import com.ch.resources.FormSubmissionResource;
import com.ch.resources.HealthcheckResource;
import com.ch.resources.HomeResource;
import com.ch.resources.PresenterAuthResource;
import com.ch.resources.QueueResource;
import com.ch.resources.TestResource;
import com.ch.service.LoggingService;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;

import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.client.proxy.ProxyConfiguration;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.ws.rs.client.Client;


/**
 * Created by Aaron.Witter on 07/03/2016.
 */
@SuppressWarnings("PMD")
public class FormsServiceApplication extends Application<FormsServiceConfiguration> {

  public static final String NAME = "Forms API Service";

  public static final MetricRegistry registry = new MetricRegistry();

  public static void main(String[] args) throws Exception {
    new FormsServiceApplication().run(args);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void initialize(Bootstrap<FormsServiceConfiguration> bootstrap) {
    bootstrap.addBundle(new TemplateConfigBundle());
    bootstrap.addBundle(new MultiPartBundle());
  }

  @Override
  public void run(FormsServiceConfiguration configuration, Environment environment) throws IOException {
    // Logging
    if (configuration.getFluentLoggingConfiguration().isFluentLoggingOn()) {
      LoggingService.setFluentLogging(configuration.getFluentLoggingConfiguration());
    }
    LoggingService.log(tag, INFO, "Starting up Forms API Service...", FormsServiceApplication.class);

    // Authentication Filter for resources
    BasicCredentialAuthFilter authFilter = new BasicCredentialAuthFilter.Builder<FormsApiUser>()
      .setAuthenticator(new FormsApiAuthenticator(configuration))
      .setRealm(getName())
      .buildAuthFilter();

    AuthDynamicFeature feature = new AuthDynamicFeature(authFilter);
    environment.jersey().register(feature);

    // MongoDB
    MongoHelper.init(configuration);

    final JerseyClientConfiguration jerseyClientConfig = configuration.getJerseyClientConfiguration();

    // Jersey Client
    final Client client = new JerseyClientBuilder(environment)
      .using(jerseyClientConfig)
      .withProvider(MultiPartFeature.class)
      .build(getName());

    final ClientHelper clientHelper = new ClientHelper(client);
    final PresenterHelper presenterHelper = new PresenterHelper(client, configuration.getCompaniesHouseConfiguration());

    SalesforceConfiguration salesForceConfiguration = configuration.getSalesforceConfiguration();
    final SalesforceClientHelper salesforceClientHelper;

    if (salesForceConfiguration.isApiUseProxy()) {

      // Jersey client with proxy

      final ProxyConfiguration proxyConfiguration = new ProxyConfiguration(salesForceConfiguration.getApiProxyHost(),
        salesForceConfiguration.getApiProxyPort());

      jerseyClientConfig.setProxyConfiguration(proxyConfiguration);

      final Client proxyClient = new JerseyClientBuilder(environment)
        .using(jerseyClientConfig)
        .build(getName() + " Via Proxy");

      salesforceClientHelper = new SalesforceClientHelper(proxyClient);
    } else {
      salesforceClientHelper = new SalesforceClientHelper(client);
    }

    // Resources
    environment.jersey().register(new FormResponseResource(salesforceClientHelper, salesForceConfiguration));
    environment.jersey().register(new FormSubmissionResource());
    environment.jersey().register(new FormResponseResource(salesforceClientHelper, salesForceConfiguration));
    environment.jersey().register(new HomeResource());
    environment.jersey().register(new HealthcheckResource());
    environment.jersey().register(new BarcodeResource(clientHelper, configuration.getCompaniesHouseConfiguration()));
    environment.jersey().register(new QueueResource(clientHelper, configuration.getCompaniesHouseConfiguration()));
    environment.jersey().register(new PresenterAuthResource(presenterHelper));
    environment.jersey().register(new ConfirmPaymentResource());

    if (configuration.isTestMode()) {
      environment.jersey().register(new TestResource());
    }

    // Health Checks
    environment.healthChecks().register("AppHealthCheck", new AppHealthCheck());
    environment.healthChecks().register("MongoHealthCheck", new MongoHealthCheck());


    // Health Checks
    final AppHealthCheck healthCheck =
      new AppHealthCheck();
    environment.healthChecks().register("AppHealthCheck", healthCheck);

    // Exception Mappers
    environment.jersey().register(new ConnectionExceptionMapper());
    environment.jersey().register(new ContentTypeExceptionMapper());
    environment.jersey().register(new MissingRequiredDataExceptionMapper());
    environment.jersey().register(new XmlExceptionMapper());
    environment.jersey().register(new XsdValidationExceptionMapper());
    environment.jersey().register(new PackageContentsExceptionMapper());
    environment.jersey().register(new DatabaseExceptionMapper());
    environment.jersey().register(new PresenterAuthenticationExceptionMapper());

    // Logging filter for input and output
    List<String> fineLevelRequestPaths = new ArrayList<String>();
    fineLevelRequestPaths.add("healthcheck");
    environment.jersey().register(new LoggingFilter(
      Logger.getLogger(LoggingFilter.class.getName()),
      true,
      fineLevelRequestPaths)
    );

    //Filters
    environment.servlets().addFilter("RateLimitFilter", new RateLimitFilter(configuration.getRateLimit()))
      .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    environment.admin().addFilter("PreventAccessFilter", new PreventAccessFilter())
      .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    // Metrics
    startReporting(configuration);
  }


  private void startReporting(FormsServiceConfiguration configuration) {
    Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
      .outputTo(LoggerFactory.getLogger(FormsServiceApplication.class))
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build();
    // report metrics to log every hour
    reporter.start(configuration.getLog4jConfiguration().getFrequency(), TimeUnit.valueOf(configuration.getLog4jConfiguration()
      .getTimeUnit().toUpperCase()));
  }
}
