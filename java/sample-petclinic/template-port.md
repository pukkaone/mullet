This application is modified to use Mullet templates to render the views.


## View resolver


## Layout


## Disable layout


## Application resource URLs

The original JSP files use the `spring:url` tag to generate URLs to resources
served by the application.  The `spring:url` tag (and the JSTL `c:url` tag)
necessarily prepends the context path to the generated URL because the context
path is decided when the application is deployed.  The developer cannot
assume the application will run under a specific context path.

Replacing each `spring:url` tag with a variable is an onerous task when a page
has a lot of links because you must set those variables in the model passed to
the template.  I chose instead to use the `base` HTML element, which specifies
a base URL to prepend to relative URLs found in the document.  I set the base
URL to the application content path. and refer to application resources using
relative URLs.  The template view makes a variable named `baseURL` available to
the template.  Its value is the context path with a trailing '/' character.


## Add URL properties to model classes

I added the properties `editUrl` and `showUrl` to the Owner and Pet classes.
The entity ID is encoded in these URLs, so templates read these properties to
render links to actions on specific entity instances.
