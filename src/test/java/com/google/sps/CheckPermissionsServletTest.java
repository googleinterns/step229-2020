package com.google.sps;

import com.google.sps.servlets.CheckPermissionsServlet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.*;
import org.apache.commons.io.FileUtils;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Ignore;

public class CheckPermissionsServletTest {

  private CheckPermissionsServlet checkPermissions;

  @Mock
  HttpServletRequest request = mock(HttpServletRequest.class);

  @Mock
  HttpServletResponse response = mock(HttpServletResponse.class);

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCorrectInput() throws Exception {

    when(request.getParameter("projID")).thenReturn("bt-dataflow-sql-demo");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    checkPermissions = new CheckPermissionsServlet();
    checkPermissions.doGet(request,response);

    writer.flush();

    assertTrue(stringWriter.toString().contains(
      "[{\"permissions\":[\"compute.instanceGroupManagers.update\",\"compute.instances.delete\",\"compute.instances.setDiskAutoDelete\"," +
        "\"dataflow.jobs.get\",\"logging.logEntries.create\",\"storage.objects.create\",\"storage.objects.get\"]},0]"));
  }
}