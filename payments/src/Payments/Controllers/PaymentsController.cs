using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net.Http;
using System.Runtime.CompilerServices;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using OpenTracing;
using OpenTracing.Tag;
using Payments.Models;

namespace Payments.Controllers
{
    [ApiController]
    public class PaymentsController : Controller
    {
        private readonly Random rand = new Random();
        private readonly ITracer tracer;
        private readonly HttpClient httpClient;
        private readonly ConcurrentDictionary<string, StrongBox<int>> internalCounters;

        public PaymentsController(ITracer tracer, ConcurrentDictionary<string, StrongBox<int>> internalCounters)
        {
            this.tracer = tracer;
            this.httpClient = new HttpClient();
            this.internalCounters = internalCounters;
            this.internalCounters.GetOrAdd("authorize", new StrongBox<int>());
        }

        // POST pay
        [Route("pay/{orderNum}")]
        [HttpPost]
        public IActionResult Pay(string orderNum, Payment payment)
        {
            if (rand.NextDouble() < 0.01)
            {
                string url = $"{Request.Scheme}://{Request.Host.ToUriComponent()}/health";
                Task.Run(async () => await httpClient.GetAsync(url));
            }

            ConsumeCpu((long)Math.Max(RandomGauss(40, 10), 20), 0.4);

            try
            {
                if (string.IsNullOrWhiteSpace(orderNum))
                {
                    throw new ArgumentException($"invalid order number: {orderNum}");
                }
                else if (string.IsNullOrWhiteSpace(payment.Name))
                {
                    throw new ArgumentException($"invalid name: {payment.Name}");
                }
                else if (string.IsNullOrWhiteSpace(payment.CreditCardNum) || rand.NextDouble() < 0.01)
                {
                    throw new ArgumentException($"invalid credit card number: {payment.CreditCardNum}");
                }
            }
            catch (Exception e)
            {
                LogException(e, null);
                throw e;
            }

            var context = tracer.ActiveSpan.Context;
            IActionResult result = rand.NextDouble() < 0 ? FastPay(context) : ProcessPayment(context);
            ConsumeCpu((long)Math.Max(RandomGauss(20, 5), 10), 0.7);

            Task.Run(async () => await UpdateAccountAsync(context));
            ConsumeCpu((long)Math.Max(RandomGauss(10, 2), 5), 1);

            return result;
        }

        private IActionResult FastPay(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("FastPay")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                try
                {
                    ConsumeCpu((long)Math.Max(RandomGauss(80, 20), 40), 0.4);
                    if (rand.NextDouble() < 0.001)
                    {
                        throw new NullReferenceException();
                    }
                    return Accepted("fast pay accepted");
                }
                catch (Exception e)
                {
                    LogException(e, null);
                    return StatusCode(StatusCodes.Status500InternalServerError, "fast pay failed");
                }
            }
        }

        private IActionResult ProcessPayment(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("ProcessPayment")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                try
                {
                    ConsumeCpu((long)Math.Max(RandomGauss(25, 5), 15), 0.7);
                    if (rand.NextDouble() < 0.001)
                    {
                        throw new OutOfMemoryException();
                    }
                    if (!AuthorizePayment(scope.Span.Context))
                    {
                        scope.Span.SetTag(Tags.Error, true);
                        return StatusCode(StatusCodes.Status500InternalServerError, "payment authorization failed");
                    }
                    ConsumeCpu((long)Math.Max(RandomGauss(15, 3), 10), 1);
                    return FinishPayment(scope.Span.Context);
                }
                catch (Exception e)
                {
                    LogException(e, null);
                    return StatusCode(StatusCodes.Status500InternalServerError, "payment processing failed");
                }
            }
        }

        private bool AuthorizePayment(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("AuthorizePayment")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                try
                {
                    int hour = DateTime.UtcNow.Hour;
                    ConsumeCpu((long)Math.Max(RandomGauss(100, 25), 50), 0.7);
                    int count = Interlocked.Increment(ref internalCounters["authorize"].Value);
                    bool timeout;
                    int timeoutMillis;
                    // 6-hour cycles with spikes every 20 calls
                    switch (hour % 12)
                    {
                        case 0:
                            // 0.5% errors
                            timeout = rand.NextDouble() < 0.005;
                            timeoutMillis = rand.Next(275, 325); //300
                            break;
                        case 1:
                            // 1% errors
                            timeout = rand.NextDouble() < 0.01;
                            timeoutMillis = rand.Next(325, 375); //350
                            break;
                        case 2:
                            // 5% errors
                            timeout = rand.NextDouble() < 0.05;
                            timeoutMillis = rand.Next(460, 540); //500
                            break;
                        case 3:
                            // 12% errors
                            timeout = rand.NextDouble() < 0.12;
                            timeoutMillis = rand.Next(650, 750); //700
                            break;
                        case 4:
                            // 7% errors
                            timeout = rand.NextDouble() < 0.07;
                            timeoutMillis = rand.Next(550, 650); //600
                            break;
                        default:
                            // 1% errors
                            timeout = rand.NextDouble() < 0.005;
                            timeoutMillis = rand.Next(275, 325); //300
                            break;
                    }

                    if (timeout)
                    {
                        ConsumeCpu(timeoutMillis, 0.5);
                        throw new TimeoutException();
                    }
                    return true;
                }
                catch (Exception e)
                {
                    LogException(e, "payment authorization timed out, please retry");
                    return false;
                }
            }
        }

        private IActionResult FinishPayment(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("FinishPayment")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                try
                {
                    ConsumeCpu((long)Math.Max(RandomGauss(60, 15), 30), 0.5);
                    if (rand.NextDouble() < 0.001)
                    {
                        throw new Exception("Payment Denied");
                    }
                    return Accepted("payment accepted");
                }
                catch (Exception e)
                {
                    LogException(e, null);
                    return StatusCode(StatusCodes.Status500InternalServerError, "finish payment failed");
                }
            }
        }

        private async Task UpdateAccountAsync(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("UpdateAccountAsync")
                    .AddReference(References.FollowsFrom, context)
                    .StartActive())
            {
                try
                {
                    double randDuration = rand.NextDouble() / 3;
                    await Task.Delay(TimeSpan.FromSeconds(1 + randDuration));
                    if (rand.NextDouble() < 0.001)
                    {
                        throw new OutOfMemoryException();
                    }
                }
                catch (Exception e)
                {
                    LogException(e, null);
                }
            }
        }

        // GET health
        [Route("health")]
        [HttpGet]
        public async Task<IActionResult> GetHealthAsync()
        {
            Thread.Sleep(TimeSpan.FromMilliseconds(Math.Max(RandomGauss(20, 5), 10)));
            var context = tracer.ActiveSpan.Context;
            if (await CheckHealthAsync(context))
            {
                tracer.ActiveSpan.Log(new Dictionary<string, object>
                {
                    { LogFields.Message, "Service is healthy" }
                });
                return Ok("healthy");
            }
            tracer.ActiveSpan.Log(new Dictionary<string, object>
            {
                { LogFields.Message, "Service is unavailable" }
            });
            return StatusCode(StatusCodes.Status503ServiceUnavailable, "unavailable");
        }

        private async Task<bool> CheckHealthAsync(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("CheckHealthAsync")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                Thread.Sleep(TimeSpan.FromMilliseconds(Math.Max(RandomGauss(20, 5), 10)));
                var taskDb = CheckDbHealthAsync(scope.Span.Context);
                var taskAuth = CheckAuthHealthAsync(scope.Span.Context);
                var results = await Task.WhenAll(taskDb, taskAuth);
                foreach (bool healthy in results)
                {
                    if (!healthy)
                    {
                        scope.Span.SetTag(Tags.Error, true);
                        scope.Span.Log("Health check failed");
                        return false;
                    }
                }
                return true;
            }
        }

        private async Task<bool> CheckDbHealthAsync(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("CheckDbHealthAsync")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                await Task.Delay(TimeSpan.FromMilliseconds(Math.Max(RandomGauss(50, 10), 30)));
                if (rand.NextDouble() < 0.01)
                {
                    scope.Span.SetTag(Tags.Error, true);
                    scope.Span.Log("Unable to connect to FoundationDB server on '127.0.0.1'");
                    return false;
                }
                return true;
            }
        }

        private async Task<bool> CheckAuthHealthAsync(ISpanContext context)
        {
            using (var scope = tracer.BuildSpan("CheckAuthHealthAsync")
                    .AddReference(References.ChildOf, context)
                    .StartActive())
            {
                await Task.Delay(TimeSpan.FromMilliseconds(Math.Max(RandomGauss(45, 10), 25)));
                if (rand.NextDouble() < 0.01)
                {
                    scope.Span.SetTag(Tags.Error, true);
                    scope.Span.Log("Unable to connect to authentication server on '127.0.0.1'");
                    return false;
                }
                return true;
            }
        }

        private double RandomGauss(double mean, double stdDev)
        {
            double u1 = 1.0 - rand.NextDouble();
            double u2 = 1.0 - rand.NextDouble();
            double randStdNormal = Math.Sqrt(-2.0 * Math.Log(u1)) *
                         Math.Sin(2.0 * Math.PI * u2);
            double randNormal =
                         mean + stdDev * randStdNormal;
            return randNormal;
        }

        private void ConsumeCpu(long millis, double pct)
        {
            Stopwatch watch = new Stopwatch();
            if (pct >= 1 || double.Equals(pct, 1))
            {
                watch.Start();
                while (watch.ElapsedMilliseconds < millis) { }
            }
            else
            {
                int chunks = 10;
                if (chunks * 3 < millis)
                {
                    chunks = 1;
                }
                int chunkMillis = Convert.ToInt32(millis / chunks);
                int loadedMillis = Convert.ToInt32(chunkMillis * pct);

                for (int i = 0; i < chunks; i++)
                {
                    watch.Start();
                    while (watch.ElapsedMilliseconds < loadedMillis) { }

                    Thread.Sleep(TimeSpan.FromMilliseconds(Math.Max(chunkMillis - watch.ElapsedMilliseconds, 0)));
                }
            }

        }

        private void LogException(Exception exception, string message)
        {
            var span = tracer.ActiveSpan;
            if (span == null)
            {
                return;
            }

            span.SetTag(Tags.Error, true);
            var fields = new Dictionary<string, object>
            {
                { LogFields.Event, Tags.Error.Key },
                { LogFields.ErrorKind, exception.GetType().Name },
                { LogFields.ErrorObject, exception }
            };
            if (!string.IsNullOrEmpty(message))
            {
                fields.Add(LogFields.Message, message);
            }
            span.Log(fields);
        }
    }
}
