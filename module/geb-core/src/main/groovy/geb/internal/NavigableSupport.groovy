/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.internal

import geb.navigator.Navigator
import geb.error.UndefinedPageContentException
import geb.error.UnresolvablePropertyException

/**
 * Classes who mix this in must implement the following methods:
 */
class NavigableSupport implements Navigable {

	private owner
	private contentTemplates
	private navigatoryFactory
	
	NavigableSupport(owner, contentTemplates, navigatoryFactory) {
		this.owner = owner
		this.contentTemplates = contentTemplates ?: [:]
		this.navigatoryFactory = navigatoryFactory
	}
	
	private getNavigator() {
		navigatoryFactory()
	}
	
	private getContent(String name, Object[] args) {
		def contentTemplate = contentTemplates[name]
		if (contentTemplate) {
			contentTemplate.get(*args)
		} else {
			throw new UndefinedPageContentException(this, name)
		}
	}
	
	Navigator $() {
		getNavigator()
	}
	
	Navigator $(int index) {
		getNavigator()[index]
	}
	
	Navigator $(String selector) {
		getNavigator().find(selector)
	}
	
	Navigator $(Map attributes) {
		getNavigator().find(attributes)
	}
	
	Navigator $(String selector, int index) {
		getNavigator().find(selector, index)
	}
	
	Navigator $(Map attributes, String selector) {
		getNavigator().find(attributes, selector)
	}
	
	/*
	-- Not implemented by Navigator
	Navigator $(Map attributes, int index) {
		getNavigator().find(attributes, index)
	}

	-- Not implemented by Navigator	
	Navigator $(Map attributes, String selector, int index) {
		getNavigator().find(attributes, selector, index)
	}
	*/

	def methodMissing(String name, args) {
		try {
			getContent(name, *args) 
		} catch (UndefinedPageContentException e1) {
			getNavigator()."$name"(*args)
		}
	}

	def propertyMissing(String name) {
		try {
			getContent(name) 
		} catch (UndefinedPageContentException e1) {
			try {
				getNavigator()."$name"
			} catch (MissingPropertyException e2) {
				throw new UnresolvablePropertyException(owner, name, "Unable to resolve $name as content for ${owner}, or as a property on it's Navigator context")
			}
		}
	}
	
	def propertyMissing(String name, val) {
		try {
			getNavigator()."$name" = val
		} catch (MissingPropertyException e) {
			throw new UnresolvablePropertyException(owner, name, "Unable to resolve $name as a property to set on ${owner}'s Navigator context")
		}
	}

}