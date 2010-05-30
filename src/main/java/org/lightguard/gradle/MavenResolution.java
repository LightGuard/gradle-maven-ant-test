package org.lightguard.gradle;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.tools.ant.BuildException;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;

public class MavenResolution
{
    private PlexusContainer plexusContainer;
    private MavenProjectBuilder mavenProjectBuilder;
    private ArtifactRepositoryFactory artifactRepositoryFactory;
    private ArtifactFactory artifactFactory;

    private ArtifactRepository localRepo;
    private List<ArtifactRepository> additionalRepos;

    public MavenResolution()
    {
        try
        {
            ClassWorld classWorld = new ClassWorld();

            classWorld.newRealm( "plexus.core", PlexusContainer.class.getClassLoader() );

            Embedder embedder = new Embedder();

            embedder.start( classWorld );

            this.plexusContainer = embedder.getContainer();
        }
        catch ( PlexusContainerException e )
        {
            throw new BuildException( "Unable to start embedder", e );
        }
        catch ( DuplicateRealmException e )
        {
            throw new BuildException( "Unable to create embedder ClassRealm", e );
        }

        this.mavenProjectBuilder = ( MavenProjectBuilder ) this.plexusLookup( MavenProjectBuilder.ROLE );
        this.artifactFactory = ( ArtifactFactory ) this.plexusLookup( ArtifactFactory.ROLE );
        this.artifactRepositoryFactory = ( ArtifactRepositoryFactory ) this.plexusLookup( ArtifactRepositoryFactory.ROLE );

        this.additionalRepos = new ArrayList<ArtifactRepository>();
    }

    public MavenProject findPom( final String artifactNotation )
    {
        String[] artifactParts = artifactNotation.split( ":" );

        Artifact pom = this.artifactFactory.createArtifact( artifactParts[0], artifactParts[1], artifactParts[2], "compile",
                                                            "pom" );

        try
        {
            return this.mavenProjectBuilder.buildFromRepository( pom, this.additionalRepos, this.localRepo );
        }
        catch ( ProjectBuildingException e )
        {
            throw new RuntimeException( "Could not build project", e );
        }
    }

    public void addRemoteRepository( String name, String url )
    {
        this.addRemoteRepository( name, url, new DefaultRepositoryLayout() );
    }

    public void addRemoteRepository( String name, String url, ArtifactRepositoryLayout layout )
    {
        this.addRemoteRepository( name, url, layout, new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy() );
    }

    public void addRemoteRepository( String name, String url, ArtifactRepositoryLayout layout,
                                     ArtifactRepositoryPolicy snapshotPolicy, ArtifactRepositoryPolicy releasePolicy )
    {
        ArtifactRepository repo = this.artifactRepositoryFactory.createArtifactRepository( name, url, layout, snapshotPolicy,
                                                                                           releasePolicy );
        this.additionalRepos.add( repo );
    }

    public void addRemoteRepository( ArtifactRepository repo )
    {
        this.additionalRepos.add( repo );
    }

    public void setLocalGradleCache( final String location )
    {
        if ( location == null || "".equals( location.trim() ) )
        {
            throw new IllegalArgumentException( "location cannot be null or empty" );
        }

        final String correctLocation = ( location.startsWith( "file://" ) ) ? location : "file://" + location;

        this.localRepo = this.artifactRepositoryFactory.createArtifactRepository( "gradleCache", correctLocation,
                                                                                  new GradleRepositoryLayout(),
                                                                                  new ArtifactRepositoryPolicy(),
                                                                                  new ArtifactRepositoryPolicy() );
    }

    private Object plexusLookup( final String role )
    {
        try
        {
            return this.plexusContainer.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new RuntimeException( MessageFormat.format( "Error looking up {0} from plexus", role ) );
        }
    }
}
