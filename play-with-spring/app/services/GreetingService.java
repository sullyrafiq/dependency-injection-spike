package services;

import org.springframework.stereotype.Service;

import javax.inject.Named;

/**
 *
 */
@Named
public class GreetingService {

    public String hello() {
        return "Hello World!";
    }

}
