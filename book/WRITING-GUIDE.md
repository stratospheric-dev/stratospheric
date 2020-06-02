# Reflectoring Writing Guidelines


## Reflectoring Mission Statement

The reflectoring blog aims to provide software developers with a **comprehensive** but **easy-to-read** learning experience that generates **“aha” moments** when they need to **solve a specific problem**.



*   **An article solves at least one specific problem:** it explains how to solve a reader’s current question and provides working code examples for them to learn from. 
*   **An article is comprehensive:** it explains a certain topic, framework feature, or solution from top-to-bottom, potentially answering questions the reader doesn’t even know about yet.
*   **An article is easy to read:** it is structured logically, uses conversational language with short sentences and paragraphs and without fancy words.
*   **An article generates “aha” moments:** it explains the “why” a certain solution works and in which cases the solution may not be the best one.


## General Guidelines


### Example Articles

For your orientation, here’s a list of some of the most successful articles on reflectoring (success = high number of readers and positive reader feedback):



*   [https://reflectoring.io/spring-boot-test/](https://reflectoring.io/spring-boot-test/)
*   [https://reflectoring.io/bean-validation-with-spring-boot/](https://reflectoring.io/bean-validation-with-spring-boot/)
*   [https://reflectoring.io/unit-testing-spring-boot/](https://reflectoring.io/unit-testing-spring-boot/)
*   [https://reflectoring.io/spring-boot-conditionals/](https://reflectoring.io/spring-boot-conditionals/)
*   [https://reflectoring.io/spring-boot-data-jpa-test/](https://reflectoring.io/spring-boot-data-jpa-test/)

These articles have in common that they explain a certain Spring Boot feature in-depth, answering a specific question the reader probably googled for, and more questions the reader didn’t even have yet. 

It’s important to note that** these articles don’t just reiterate the content of Spring Boot’s reference manual**, but instead explain the features in simple words with code examples and sections that explain **why **we should or should not do it in a certain way.


### Article Categories

The main categories of the reflectoring blog contain tutorials about the** [Java](https://reflectoring.io/categories/java) **programming language in general and the **[Spring Boot](https://reflectoring.io/categories/spring-boot)** framework in particular. Also of interest are articles about software development and architecture best practices in a category called **[Software Craft](https://reflectoring.io/categories/craft)**.


### Article Length

There is no hard-and-fast rule for how long an article should be. Given the goal of comprehensiveness, however,  good reflectoring articles tend to be between 1000 and 2000 words (including code examples).

You can use [WordCounter](https://wordcounter.net/) to count the words of your article.


## Language Guidelines


### Use Simple Language

Online readers don’t want to spend much time on understanding a topic. It’s important to keep the text simple and not use words that are ambiguous or difficult to understand.

Some examples:


<table>
  <tr>
   <td><strong>Don’t do this</strong>
   </td>
   <td><strong>Do this instead</strong>
   </td>
  </tr>
  <tr>
   <td>“utilize”
   </td>
   <td>“use”
   </td>
  </tr>
  <tr>
   <td>“In order to”
   </td>
   <td>“to”
   </td>
  </tr>
</table>


Copy your text into [Grammarly](https://app.grammarly.com/), which provides some great simplification suggestions even on the free tier.


### Keep Sentences and Paragraphs Short

Instead of one long sentence, use two short ones. Sub-clauses often make the text harder to read and introduce ambiguity.

Instead of a wall of text, split the text into logical paragraphs of ideally no more than 4 lines. Important statements can sometimes even be a paragraph in their own right, even if it’s a one-liner.


### Create a Conversation with the Reader

Texts are more engaging if they read like a conversation between you and the reader. This means that you can use the pronouns “I” and “You”, as we would when speaking to someone.

When explaining how to do something, however, use “we” rather than “you”, as too much “do this” and “do that” can quickly sound condescending (I’m aware that I’m doing it this document :)).


<table>
  <tr>
   <td><strong>Don’t do this</strong>
   </td>
   <td><strong>Do this instead</strong>
   </td>
  </tr>
  <tr>
   <td>“Add an annotation to class X to do Y”
   </td>
   <td>“We add an annotation to class X to do Y”
   </td>
  </tr>
  <tr>
   <td>“The next step is to do X”
   </td>
   <td>“Let’s do X next”
   </td>
  </tr>
</table>



### Be Inclusive

We don’t want our texts to offend anyone, so make sure to use inclusive language. Don’t assume the gender of people you use in examples. Use plural instead. Where plural isn’t applicable, use it anyways (it’s called the “single they”).


<table>
  <tr>
   <td><strong>Don’t do this</strong>
   </td>
   <td><strong>Do this instead</strong>
   </td>
  </tr>
  <tr>
   <td>“These guys...”
   </td>
   <td>“These developers...”
   </td>
  </tr>
  <tr>
   <td>“By doing this, we make life easier for the developer. He will thank you for it.”
   </td>
   <td>“By doing this, we make life easier for the developers. They will thank you for it.”
<p>
or, using “single they”:
<p>
“By doing this, we make life easier for the developer. They will thank you for it.”
   </td>
  </tr>
</table>



### Make It Personal

Include something of yourself in the text. If you have made some experience that connects to the topic at hand, share it.

Add a sentence in parentheses to comment on something (I sometimes share my thoughts in parentheses like this).

Add a bit of dry humor if the situation allows it. We’re not writing a doctoral thesis that no one really understands.


### Use Active, Not Passive

Usive active voice instead of passive voice wherever possible. This makes the reading much less convoluted and thus easier.


<table>
  <tr>
   <td><strong>Don’t do this</strong>
   </td>
   <td><strong>Do this instead</strong>
   </td>
  </tr>
  <tr>
   <td>“This can be done by…”
   </td>
   <td>“We can do this by...”
   </td>
  </tr>
  <tr>
   <td>“This code will be executed by method XYZ.”
   </td>
   <td>“Method XYZ executes this code.”
   </td>
  </tr>
</table>



## Conventions


### Be Consistent

Be consistent about spelling. 

Check the spelling of frameworks, libraries, and products so that it matches their brand name.

Some words can be spelled in different variations. Stick to one of them throughout the text.


### Introduce the Article

Start the article with a sentence or two about what to expect in the article. Don’t drop the readers into cold water. Give them a chance to drop out of reading right then and there if the topic is not interesting for the.

Try to make the introduction compelling, though. Ask open questions that the article will answer to spark curiosity.


### Conclude the Article

Conclude the article with a … wait for it … conclusion. Summarize the key takeaways from the article in a sentence or two. Add a joke to the end if you can think of one so that the reader is rewarded for reading to the end.


### Use Title Case in Headers

Use title case in headings. Check your headings on [titlecase.com](http://titlecase.com/) for the correct capitalization.


### Highlight Important Key Facts in Bold

Internet readers usually don’t read an article from start to end, but they scan it. Help the “scanner”-type readers, by making the main ideas of the articles bold. Don’t make single words bold, because they have too little context for scanning. Also, don’t make whole paragraphs bold, because it won’t help the “scanner” to find the interesting bits.

Instead, **highlight sentences and half-sentences in bold that carry a main idea and make sense without reading the rest of the text**.


### Links

Link to sources you used while researching for the article. These can be reference manuals, or other any other website links. 

Make the link part of a natural sentence instead of adding a word just for the link.


<table>
  <tr>
   <td><strong>Don’t do this</strong>
   </td>
   <td><strong>Do this instead</strong>
   </td>
  </tr>
  <tr>
   <td>“You can find the reference manual <a href="www.example.com">here</a>.”
   </td>
   <td>“You can find more information in the <a href="www.example.com">reference manual</a>.”
   </td>
  </tr>
</table>



## Quality


### Review Your Text After a Day

When you’re done writing, leave the text alone for a couple of hours or a day. Then, read through it with a fresh mind and fix all those complicated phrases and typos. You’ll be amazed at what issues you find after having your mind do something else for a while.


### Check Your Text with Grammarly

After your own review, please paste it into the free tier service of [Grammarly ](https://app.grammarly.com/)and apply all suggestions. They usually make sense. If a suggestion doesn’t make sense, don’t apply it.


### Add Cross-Links to Other reflectoring Articles

Do a quick Google search restricted to “site:reflectoring.io” to find out if there are other articles about a similar topic. If yes, think of a way to naturally link to them within the article to create 


## Working with Code Examples


### Prove Your Claims with Code Examples

Almost every reflectoring article should be accompanied by a working code example. The code examples are collected in the [code-examples](https://github.com/thombergs/code-examples) GitHub repository. Add your code to an existing module if it fits, or create a new module if there is no fitting module.

The code examples in the article should always be copied from the real code in the code-examples repository so that we can be sure they are correct.

Code modules can use Maven or Gradle as a build tool. Make sure to include the [Maven Wrapper](https://github.com/takari/maven-wrapper) or [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) so that they can run anywhere.


### Introduce a Code Example, Then Explain 

Introduce a code example with a sentence like “Let’s take a look at the Foo class:” (note the colon “:”). Then paste the code example.

Below the code example, explain it step by step.

Don’t start to explain code before the code example. The reader hasn’t had a chance to see the code, yet!


### Explain Code Bottom-Up

If the code in one code example depends on the code of another code example, start with the independent one and only add the dependent code example afterward. This is the natural order of things and the reader’s mind can keep up.


### Keep Code Examples Small

If a code example contains boilerplate code like getters and setters or methods that are irrelevant to the discussion, remove the irrelevant code from the example and replace it with a comment like “`// other methods omitted`” or “`// …`”.

Make the code examples as small and understandable as possible.


### Don’t Modify Code Examples Manually

Except for omitting code as explained above, don’t modify code manually. If you changed something in the real code, copy and paste it into the article instead of modifying the code example in the article manually. Errors will creep in otherwise.


### Use Package-Private Visibility

Don’t use the public modifier as the default. Use package-private visibility where possible. This keeps the code examples more focused on the important things. Also, it’s a good practice for dependency hygiene and we want to teach good practices :).


### Format Code

When you copy the code example into the article, reduce the indentation to 2 spaces to make it more compact and to reduce the chance for scrolling. You can do this by searching and replacing the existing indentation.


### Link to the Code Examples Repository

Link to the code example in the repositories at the start and the end of the article. Link directly to the module that contains your example, for instance, [https://github.com/thombergs/code-examples/tree/master/logging](https://github.com/thombergs/code-examples/tree/master/logging). 

At the start of the article, use this include after the introductory paragraph:


```
{% include github-project.html url="link_to_example_module" %}
```


This will include a Heading with a little default text and a link.

At the end of the article, remind the reader that he can look up the code on [GitHub](https://github.com/thombergs/code-examples/tree/master/logging) like this.
