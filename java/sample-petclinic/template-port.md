This application is modified to use Mullet templates to render the views.


## View resolver


## Layout


## Disable layout


## Application resource URLs

The context path affects the URL where the browser should send requests for
application resources, such as where to post form submissions.  The context
path is decided when the application is deployed, so the developer can't assume
the application will run under a specific context path.  The `spring:url` tag
(and the JSTL `c:url` tag) takes a relative URL and generates a URL the browser
can use by prepending the context path.

I could replace each `spring:url` tag with a variable, but that's an onerous
task because I would have to set a variable for each link I want to render.  I
chose a solution requiring only one variable.  The `base` HTML element sets the
base URL of resources specified by a relative URL.  The view implicitly sets a
variable named `contextPathURL` to the value of the context path.  The template
renders that variable in the `base` element, and I changed the links specified
by `spring:url` to plain relative URLs.


## Decorators for model classes

I added classes to decorate the Owner and Pet classes with URL properties that
templates will read to render links to actions performed on those specific
entity instances.
