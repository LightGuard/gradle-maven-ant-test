package org.lightguard.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.ant.DependenciesTask;
import org.apache.maven.model.Dependency;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;

public class MavenArtifactResolution
{
    public Collection<File> retrieveArtifacts( List<String> dependencies )
    {
        return this.retrieveArtifacts( dependencies, "compile" );
    }

    public Collection<File> retrieveArtifacts( List<String> dependencies, String scope )
    {
        DependenciesTask task = new DependenciesTask();

        task.setProject( new Project() );
        task.setPathId( "deps" );

        for ( String s : dependencies )
        {
            String[] artifactNotation = s.split( ":" );

            Dependency dep = new Dependency();
            dep.setGroupId( artifactNotation[0] );
            dep.setArtifactId( artifactNotation[1] );
            dep.setVersion( artifactNotation[2] );
            dep.setScope( scope );

            task.addDependency( dep );
        }

        task.execute();
        Path p = ( Path ) task.getProject().getReference( task.getPathId() );

        List<File> results = new ArrayList<File>();
        Iterator<FileResource> i = p.iterator();
        while ( i.hasNext() )
        {
            results.add( i.next().getFile() );
        }

        return results;
    }

//    public MavenProject parsePomWithoutPlexus( String artifactNotation ) throws ProjectBuildingException
//    {
//        String[] artifactInfo = artifactNotation.split( ":" );
//
//        ArtifactFactory af = new DefaultArtifactFactory();
//        ArtifactHandlerManager ahm = new DefaultArtifactHandlerManager();
//
//        this.injectValue( "artifactHandlers", new HashMap<String, Object>(), ahm );
//        this.injectValue( "artifactHandlerManager", ahm, af );
//
//        ArtifactRepositoryFactory arf = new DefaultArtifactRepositoryFactory();
//
//        Artifact pom = af.createArtifact( artifactInfo[0], artifactInfo[1], artifactInfo[2], "compile", "pom" );
//
//        ArtifactRepository localRepo = arf.createArtifactRepository( "local", "file:///tmp/repo", new DefaultRepositoryLayout(),
//                                                                     new ArtifactRepositoryPolicy(),
//                                                                     new ArtifactRepositoryPolicy() );
//
//        ArtifactRepository central = arf.createArtifactRepository( "cenral", "http://repo1.maven.org/maven2",
//                                                                   new DefaultRepositoryLayout(),
//                                                                   new ArtifactRepositoryPolicy(),
//                                                                   new ArtifactRepositoryPolicy() );
//
//        MavenProjectBuilder builder = new DefaultMavenProjectBuilder();
//
//        ArtifactResolver ar = new DefaultArtifactResolver();
//        ArtifactTransformationManager atm = new DefaultArtifactTransformationManager();
//        WagonManager wm = new DefaultWagonManager();
//
//        ( (DefaultWagonManager) wm).enableLogging( new ConsoleLogger( Logger.LEVEL_DEBUG, "wagonManager" ) );
//
//        this.injectValue( "artifactTransformations", Arrays.asList( new LatestArtifactTransformation() ), atm );
//        this.injectValue( "wagonManager", wm, ar );
//        this.injectValue( "transformationManager", atm, ar );
//        this.injectValue( "artifactResolver", ar, builder );
//
//        return builder.buildFromRepository( pom, Arrays.asList( central ), localRepo);
//    }

    // Keeping this around so I know how to do it later
//    private void injectValue(final String fieldName, final Object injectionPoint, final Object containerInstance)
//    {
//        AccessController.doPrivileged( new PrivilegedAction<Object>()
//        {
//            public Object run()
//            {
//                try
//                {
//                    Field f = containerInstance.getClass().getDeclaredField( fieldName );
//                    f.setAccessible( true );
//                    f.set( containerInstance, injectionPoint );
//                    return null;  // nothing to return
//                }
//                catch ( IllegalAccessException e )
//                {
//                    throw new RuntimeException( "Error with Reflection", e);
//                }
//                catch ( NoSuchFieldException e )
//                {
//                    throw new RuntimeException( "Error with Reflection", e );
//                }
//            }
//        }, AccessController.getContext() );
//    }
}
