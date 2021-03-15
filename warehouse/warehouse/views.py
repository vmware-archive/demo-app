import logging
import random
import sys
import time
import traceback
import requests
from concurrent.futures import ThreadPoolExecutor
from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.conf import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

tracing = settings.OPENTRACING_TRACING
tracer = tracing.tracer
executor = ThreadPoolExecutor(max_workers=2)


@api_view(http_method_names=["GET"])
def fetch(request, order_num):
    try:
        time.sleep(1)
        if random.randint(1, 1000) == 1000:
            raise RuntimeError("Random Service Unavailable!")
        if not order_num:
            raise ValueError("Invalid Order Num!")
        executor.submit(async_fetch, tracer.active_span)
        if random.randint(1, 3) == 3:
            requests.get(
                "http://localhost:" + request.META["SERVER_PORT"] + "/check_stock")
        return Response(
            data={"status": "Order:" + order_num + " fetched from warehouse"},
            status=202)
    except RuntimeError:
        return handle_exception(tracer.active_span, sys.exc_info(), 503)
    except ValueError:
        return handle_exception(tracer.active_span, sys.exc_info(), 400)


def async_fetch(parent_span):
    with tracer.scope_manager.activate(parent_span, finish_on_close=True):
        with tracer.start_active_span('async_fetch') as scope:
            try:
                time.sleep(0.5)
                if random.randint(1, 1000) == 1000:
                    raise RuntimeError("Fail to execute async_fetch")
                invoke_lambda(tracer.active_span)
                return
            except RuntimeError:
                handle_exception(scope.span, sys.exc_info())

def invoke_lambda(parent_span):
    with tracer.scope_manager.activate(parent_span, finish_on_close=True):
        with tracer.start_active_span('invoke_lambda',
                                      tags=[("span.kind", "client"),
                                            ("component", "java-aws-sdk"),
                                            ("peer.service", "AWSLambda")]) as scope:
            try:
                time.sleep(1.5)
                if random.randint(1, 1000) == 1000:
                    raise RuntimeError("Fail to invoke lambda")
                return
            except RuntimeError:
                handle_exception(scope.span, sys.exc_info())


@api_view(http_method_names=["GET"])
def check_stock(request):
    time.sleep(1)
    schedule_checking(tracer.active_span)
    return Response(status=202)


def schedule_checking(parent_span):
    with tracer.scope_manager.activate(parent_span, finish_on_close=True):
        with tracer.start_active_span('schedule_checking') as scope:
            time.sleep(1)
            executor.submit(async_check, scope.span)
            return


def async_check(parent_span):
    with tracer.scope_manager.activate(parent_span, finish_on_close=True):
        with tracer.start_active_span('async_check'):
            time.sleep(1)
            return


def handle_exception(active_span, exe_info, status_code=None):
    error_msg = str(exe_info[1])
    if error_msg:
        logging.warning(error_msg)
    if active_span:
        active_span.set_tag('error', 'true')
        error_log = {
            'ErrorType': str(exe_info[0].__name__),
            'ErrorContent': error_msg,
            'ErrorTraceBack':
                '\n'.join(map(str.strip, traceback.format_tb(exe_info[2])))}
        print(error_log)
        active_span.log_kv(error_log)
    if not status_code:
        return
    else:
        return Response(error_msg, status=status_code)
