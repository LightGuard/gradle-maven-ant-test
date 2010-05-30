package org.lightguard.gradle.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Test;
import org.lightguard.gradle.MavenResolution;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MavenResolutionTest
{
    @Test
    public void testConstructorCreatesPlexusContainer() throws NoSuchFieldException, IllegalAccessException
    {
        MavenResolution cut = new MavenResolution();

        PlexusContainer pc = ( PlexusContainer ) this.getValueOfInaccessableField( "plexusContainer", cut );

        assertThat( pc, is( notNullValue( PlexusContainer.class ) ) );
    }

    @Test
    public void testPlexusLookupsWork() throws ComponentLookupException
    {
        MavenResolution cut = new MavenResolution();

        MavenProjectBuilder builder = ( MavenProjectBuilder ) this.invokeMethod( "plexusLookup", cut, MavenProjectBuilder.ROLE );

        assertThat( builder, is( notNullValue( MavenProjectBuilder.class ) ) );
    }

    @Test
    public void testPomParse()
    {
        MavenResolution cut = new MavenResolution();

        cut.addRemoteRepository( "central", "http://repo1.maven.org/maven2" );
        cut.setLocalGradleCache( "/Users/jporter/.gradle/cache/" );
        MavenProject mavenProject = cut.findPom( "junit:junit:4.8.1" );

        assertThat( mavenProject, is( notNullValue( MavenProject.class ) ) );
        assertThat( mavenProject.getModel().getDescription(), is( notNullValue( String.class ) ) );
    }

    private Object getValueOfInaccessableField( final String fieldName, final Object containingObject )
    {
        return AccessController.doPrivileged( new PrivilegedAction<Object>()
        {
            public Object run()
            {
                try
                {
                    Field f = containingObject.getClass().getDeclaredField( fieldName );
                    if ( Modifier.isPrivate( f.getModifiers() ) || Modifier.isProtected( f.getModifiers() ) )
                    {
                        f.setAccessible( true );
                    }
                    return f.get( containingObject );
                }
                catch ( IllegalAccessException e )
                {
                    throw new RuntimeException( "Error with reflection", e );
                }
                catch ( NoSuchFieldException e )
                {
                    throw new RuntimeException( "Error with reflection", e );
                }
            }
        }, AccessController.getContext() );
    }

    private Object invokeMethod( final String methodName, final Object containingObject, final Object... args )
    {
        return AccessController.doPrivileged( new PrivilegedAction<Object>()
        {
            public Object run()
            {
                try
                {
                    Class[] argTypes = new Class[args.length];

                    for ( int i = 0; i < args.length; i++ )
                    {
                        argTypes[i] = args[i].getClass();
                    }

                    Method m = containingObject.getClass().getDeclaredMethod( methodName, argTypes );
                    if ( Modifier.isPrivate( m.getModifiers() ) || Modifier.isProtected( m.getModifiers() ) )
                    {
                        m.setAccessible( true );
                    }
                    return m.invoke( containingObject, args );
                }
                catch ( IllegalAccessException e )
                {
                    throw new RuntimeException( MessageFormat.format( "Error with reflection: Illegal access {0}",
                                                                      e.getMessage() ) );
                }
                catch ( NoSuchMethodException e )
                {
                    throw new RuntimeException( MessageFormat.format( "Error with reflection: No such method {0}",
                                                                      e.getMessage() ) );
                }
                catch ( InvocationTargetException e )
                {
                    throw new RuntimeException( MessageFormat.format( "Error with reflection: invocation target {0}",
                                                                      e.getMessage() ) );
                }
            }
        }, AccessController.getContext() );
    }

}
