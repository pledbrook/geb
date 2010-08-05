/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.junit4

import geb.test.util.TestHttpServer
import geb.Page
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4)
class GebReportingTestTest extends GebReportingTest {

	def server = new TestHttpServer()
	
	static counter = 0
	
	static responseText = """
		<html>
		<body>
			<div class="d1" id="d1">d1</div>
		</body>
		</html>
	"""
	
	@Before
	void setUp() {
		server.start()
		server.get = { req, res ->
			res.outputStream << responseText
		}
		super.setUp()
		go("/")
	}
	
	String getBaseUrl() {
		server.baseUrl
	}
	
	File getReportDir() {
		new File("build/geb-output")
	}
	
	def getClassReportDir() {
		new File(getReportDir(), this.class.name.replace('.', '/'))
	}
	
	def getFirstOutputFile() {
		getClassReportDir().listFiles().find { it.name.startsWith("1") }
	}
	
	@Test 
	void a() {
		if (++counter == 2) {
			doTestReport()
		}
	}
	
	@Test
	void b() {
		if (++counter == 2) {
			doTestReport()
		}
	}
	
	def doTestReport() {
		def report = getFirstOutputFile()
		assert report.exists()
		assert report.name in ["1-a.html", "1-b.html"] // can't guarantee execution order
		assert report.text.contains('<div class="d1" id="d1">')
	}
	
	@After
	void tearDown() {
		server.stop()
		super.tearDown()
	}
}