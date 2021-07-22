using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Runtime.CompilerServices;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using OpenTracing;
using Wavefront.AspNetCore.SDK.CSharp.Common;
using Wavefront.OpenTracing.SDK.CSharp;
using Wavefront.OpenTracing.SDK.CSharp.Reporting;
using Wavefront.SDK.CSharp.Common;
using Wavefront.SDK.CSharp.Common.Application;
using Wavefront.SDK.CSharp.DirectIngestion;
using Wavefront.SDK.CSharp.Proxy;

namespace Payments
{
    public class Startup
    {
//        private IHostingEnvironment env;
	private	Microsoft.AspNetCore.Hosting.IWebHostEnvironment env;

        public Startup(IConfiguration configuration, Microsoft.AspNetCore.Hosting.IWebHostEnvironment env)
        {
            Configuration = configuration;
            this.env = env;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            //services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_1);
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_3_0); 

            ConfigureWavefront(services);
	    services.AddMvc(MvcOptions => MvcOptions.EnableEndpointRouting = false);

            // register internal counters
            services.TryAddSingleton(new ConcurrentDictionary<string, StrongBox<int>>());
        }

        private void ConfigureWavefront(IServiceCollection services)
        {
            var appTagsConfig = new ConfigurationBuilder()
                .AddYamlFile(Path.Combine(env.ContentRootPath, Configuration["applicationTagsYamlFile"]))
                .Build();
            ApplicationTags applicationTags = ConstructApplicationTags(appTagsConfig);

            var wfReportingConfig = new ConfigurationBuilder()
                .AddYamlFile(Path.Combine(env.ContentRootPath, Configuration["wfReportingConfigYamlFile"]))
                .Build();

            string source = wfReportingConfig["source"];
            if (string.IsNullOrWhiteSpace(source))
            {
                source = Dns.GetHostName();
            }

            IWavefrontSender wavefrontSender = ConstructWavefrontSender(wfReportingConfig);

            WavefrontAspNetCoreReporter wfAspNetCoreReporter = new WavefrontAspNetCoreReporter
                .Builder(applicationTags)
                .WithSource(source)
                .Build(wavefrontSender);

            ITracer tracer;

            if (wfReportingConfig.GetValue<bool>("reportTraces"))
            {
                WavefrontSpanReporter wavefrontSpanReporter = new WavefrontSpanReporter
                 .Builder()
                 .WithSource(source)
                 .Build(wavefrontSender);
                var consoleReporter = new ConsoleReporter(source);
                var compositeReporter = new CompositeReporter(wavefrontSpanReporter, consoleReporter);
                tracer = new WavefrontTracer
                    .Builder(compositeReporter, applicationTags)
                    .Build();
            }
            else
            {
                tracer = null;
            }

            services.AddWavefrontForMvc(wfAspNetCoreReporter, tracer);
        }

        private ApplicationTags ConstructApplicationTags(IConfiguration appTagsConfig)
        {
            var appTagsBuilder = new ApplicationTags
                .Builder(appTagsConfig["application"], appTagsConfig["service"])
                .Cluster(appTagsConfig["cluster"])
                .Shard(appTagsConfig["shard"]);

            var customTagsConfig = appTagsConfig.GetSection("CustomTags");
            var customTagsDict = new Dictionary<string, string>();
            foreach (var tag in customTagsConfig.GetChildren())
            {
                customTagsDict.Add(tag.Key, tag.Value);
            }
            appTagsBuilder.CustomTags(customTagsDict);

            return appTagsBuilder.Build();
        }

        private IWavefrontSender ConstructWavefrontSender(IConfiguration wfReportingConfig)
        {
            string reportingMechanism = wfReportingConfig["reportingMechanism"];

            if (reportingMechanism.Equals("proxy"))
            {
                return new WavefrontProxyClient
                    .Builder(wfReportingConfig["proxyHost"])
                    .MetricsPort(wfReportingConfig.GetValue<int>("proxyMetricsPort"))
                    .DistributionPort(wfReportingConfig.GetValue<int>("proxyDistributionsPort"))
                    .TracingPort(wfReportingConfig.GetValue<int>("proxyTracingPort"))
                    .Build();
            }
            else if (reportingMechanism.Equals("direct"))
            {
                return new WavefrontDirectIngestionClient
                   .Builder(wfReportingConfig["server"], wfReportingConfig["token"])
                   .Build();
            }
            else
            {
                throw new SystemException("Invalid reporting mechanism: " + reportingMechanism);
            }
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseHsts();
            }

            app.UseHttpsRedirection();
            app.UseMvc();
        }
    }
}
