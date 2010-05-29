package org.lightguard.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.ant.DependenciesTask;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.model.Dependency;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;

public class MavenArtifactResolution
{
    public Collection<File> retrieveArtifacts( List<String> dependencies )
        throws ArtifactMetadataRetrievalException, NoSuchFieldException, IllegalAccessException
    {
        return this.retrieveArtifacts( dependencies, "compile" );
    }

    public Collection<File> retrieveArtifacts( List<String> dependencies, String scope )
        throws ArtifactMetadataRetrievalException, NoSuchFieldException, IllegalAccessException
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
}
