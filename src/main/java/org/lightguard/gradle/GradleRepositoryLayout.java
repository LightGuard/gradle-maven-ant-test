package org.lightguard.gradle;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

public class GradleRepositoryLayout implements ArtifactRepositoryLayout
{
    private static final char PATH_SEPARATOR = File.separatorChar;

    private static final char GROUP_SEPARATOR = '.';

    private static final char ARTIFACT_SEPARATOR = '-';

    public String pathOf( Artifact artifact )
    {
        ArtifactHandler artifactHandler = artifact.getArtifactHandler();

        StringBuilder path = new StringBuilder();
        StringBuilder directoryPath = new StringBuilder();

        directoryPath.append( artifact.getGroupId() ).append( PATH_SEPARATOR );
        directoryPath.append( artifact.getArtifactId() ).append( PATH_SEPARATOR );

        if ( "pom".equals( artifact.getType() ) )
        {
            path.append( "ivy" ).append( ARTIFACT_SEPARATOR );
            path.append( artifact.getVersion() ).append( GROUP_SEPARATOR );
            path.append( "xml" ).append( GROUP_SEPARATOR ).append( "original" );

            return directoryPath.append( path.toString() ).toString();
        }
        else
        {
            directoryPath.append( "jars" ).append( PATH_SEPARATOR );
            path.append( artifact.getArtifactId() ).append( ARTIFACT_SEPARATOR ).append( artifact.getVersion() );

            if ( artifact.hasClassifier() )
            {
                path.append( ARTIFACT_SEPARATOR ).append( artifact.getClassifier() );
            }

            if ( artifactHandler.getExtension() != null && artifactHandler.getExtension().length() > 0 )
            {
                path.append( GROUP_SEPARATOR ).append( artifactHandler.getExtension() );
            }

            return directoryPath.append( path.toString() ).toString();
        }
    }

    public String pathOfLocalRepositoryMetadata( final ArtifactMetadata metadata, final ArtifactRepository repository )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String pathOfRemoteRepositoryMetadata( final ArtifactMetadata metadata )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
