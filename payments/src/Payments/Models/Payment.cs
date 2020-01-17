using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;

namespace Payments.Models
{
    public class Payment
    {
        public Payment(string name, string creditCardNum)
        {
            Name = name;
            CreditCardNum = creditCardNum;
        }

        public string Name { get; set; }

        public string CreditCardNum { get; set; }
    }

}
