FROM mcr.microsoft.com/dotnet/sdk:5.0.202

COPY payments /payments
COPY *.yaml /conf/
COPY wf-config.yaml /conf/
EXPOSE 50063
CMD dotnet run -p /payments/src/Payments
