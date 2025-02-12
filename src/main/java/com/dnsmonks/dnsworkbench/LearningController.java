package com.dnsmonks.dnsworkbench;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearningController {

    @GetMapping("/learning")
    public String learning() {
        return "learning";
    }

    @GetMapping("/learning/dns-a-record")
    public String a_rr() {
        return "dns-a-record";
    }

    @GetMapping("/learning/dns-cname-record")
    public String cname_rr() {
        return "dns-cname-record";
    }

    @GetMapping("/learning/dns-mx-record")
    public String mx_rr() {
        return "dns-mx-record";
    }

    @GetMapping("/learning/dns-txt-record")
    public String txt_rr() {
        return "dns-txt-record";
    }

    @GetMapping("/learning/dns-ns-record")
    public String ns_rr() {
        return "dns-ns-record";
    }

    @GetMapping("/learning/dns-soa-record")
    public String soa_rr() {
        return "dns-soa-record";
    }

    @GetMapping("/learning/dns-srv-record")
    public String srv_rr() {
        return "dns-srv-record";
    }

    @GetMapping("/learning/dns-ptr-record")
    public String ptr_rr() {
        return "dns-ptr-record";
    }

    @GetMapping("/learning/dns-aaaa-record")
    public String aaaa_rr() {
        return "dns-aaaa-record";
    }

}
