package org.lightguard.gradle.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.junit.Test;
import org.lightguard.gradle.MavenArtifactResolution;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MavenArtifactResolutionTest
{
    @Test
    public void testDependencyResolution() throws ArtifactMetadataRetrievalException, NoSuchFieldException, IllegalAccessException
    {
        MavenArtifactResolution cut = new MavenArtifactResolution();
        List<File> result = new ArrayList<File>( cut.retrieveArtifacts( Arrays.asList( "junit:junit:4.8.1" ) ) );
        assertThat( result.isEmpty(), is( false ) );
    }

    @Test
    public void testMultipleDependencyResolution()
        throws ArtifactMetadataRetrievalException, NoSuchFieldException, IllegalAccessException
    {
        MavenArtifactResolution cut = new MavenArtifactResolution();
        List<File> result = new ArrayList<File>( cut.retrieveArtifacts( Arrays.asList( "log4j:log4j:1.2.16",
                                                                                       "junit:junit:4.8.1" ) ) );
        assertThat( result.isEmpty(), is( false ) );
        assertThat( result.size(), is( 2 ) );
    }

    @Test
    public void testTransientDependencyResolution()
        throws ArtifactMetadataRetrievalException, NoSuchFieldException, IllegalAccessException
    {
        MavenArtifactResolution cut = new MavenArtifactResolution();
        List<File> result = new ArrayList<File>( cut.retrieveArtifacts( Arrays.asList( "org.jboss.weld:weld-core:1.0.0" ) ) );
        assert result.size() > 1;
    }
}
