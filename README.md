# intentïon

intentïon: The feedback loop of innovation

`xn--intenton-z2a.com` is a project by Polycode Limited which presents the intentïon home page: https://xn--intenton-z2a.com/

## TODO

Public brand:
- [x] Website for intentïon.com text light, light grey or misty: https://xn--intenton-z2a.com/
- [x] Logo selection
- [ ] Brand ownership
- [~] CI deployment
- [x] Sign up for LinkTree
- [ ] Link to Linktree
- [ ] "What is your intentïon?" as a tagline and pronunciation guide.
- [ ] "What is your intentïon?" submission box allowing user submitted content with terms and conditions.
- [ ] Showcase links
- [ ] Audience Dashboard
- [ ] Projects links
- [ ] Web analytics
- [ ] Request email action
- [ ] Automated feed generation
- [ ] Automated activity generation from showcased projects
- [ ] Add contact bots for socials
- [ ] Add contact bots via Slack / Discord or Redit
- [ ] Brand protection
- [ ] Register: intentiion.com, intentionai.com, intentiionai.com, intentiionaii.com
- [ ] CDK deploy
- [ ] Extract CDK deploy to open source it under intentïon (by Polycode). 
- [ ] Extract CI deploy action to open source it under intentïon (by Polycode).
- [ ] Deploy through to live including some tests.
- [ ] Quick deploy for changes to the application stack only.

LinkedIn - https://www.linkedin.com/company/intentïon
```
-> intentïon.com
-> Personal LinkedIn
-> Shared showcased projects posts crediting the service or API used
-> No direct messages
-> Comments and mentions are an [Inbox]
```

Facebook - https://www.facebook.com/profile.php?id=61559328506140
```
-> intentïon.com
-> Shared showcased projects posts crediting the service or API used
-> No direct messages
-> Comments and mentions are an [Inbox]
```

Twitter - https://twitter.com/intentiionai
```
-> intentïon.com
-> Re-Tweets of showcased projects tweets crediting the service or API used
-> No direct messages
-> Replies and mentions are an [Inbox]
```

Instagram - https://www.instagram.com/intentiionaii
```
-> LinkTree
-> Shared showcased projects posts crediting the service or API used
-> No direct messages
-> Comments and mentions are an [Inbox]
```

LinkTree - https://linktr.ee/intentiion
```
-> intentïon.com
-> LinkedIn
-> Facebook
-> Twitter
-> Instagram
```

GitHub (showcase projects)
```
-> How to seed a new similar project [Goal]
-> Featured intentïon services
-> Featured intentïon APIs
-> Download and clone stats
-> CI stats
-> Attribution documents
-> Contributor guidelines
```

GitHub (projects)
```
-> How to seed a project [Goal]
-> Examples (such as the showcase projects)
-> Activity in social media
-> Discussion forum
-> Wiki
-> Ticketing system [Inbox]
```

Request email
```
-> Link on intentïon.com to a form to request contact by email [Inbox]
```

# intentïon: The Feedback Loop of Innovation

intentïon is the beginning of an iterative journey where AI meets real-world interaction and creative problem-solving. Intentïon is built around the concept that conversational AI that engages in a continuous cycle of feedback and enhancement.

Starting with a number-guessing game that teaches the AI the basics of interaction and response adjustment, intentïon paves the way for AI to step out of the virtual domain and interact with the physical environment. This project embodies the integration of AI into real-world applications, foreseeing a future where AI, with resources like 3D printing and drone technology, could manifest a tangible presence and contribute substantively to tasks that were once the sole domain of humans.

Going beyond the fundamentals, intentïon aspires to harness the vast potential of social media, envisioning an AI that can not only create and manage an online presence but also attract a following and engage in complex activities such as securing sponsorships. This leap from simple number prediction to negotiating the social media landscape marks a significant step in AI's evolution, rooted in the belief that a careful blend of machine learning and human oversight can push the boundaries of what we perceive as achievable.

What intentïon brings to the table is not just a promise but a steady march towards realizing the intersection of AI capabilities and human aspirations. It invites us to ponder, as we witness this fusion of technology and creativity unfold, "What is your intentïon?"

## Pronunciation

intentïon. Pronunciation: /ɪnˈtɛnʃən/. The diaeresis? It's a style thing (and .com was available for 13 bucks); you are invited to pronounce your intentïon as you please.

# Glossary

- **intentïon**: An *intentïon* can be *accomplished* in a single *iteration* with at least one *parameter set*.
- **iteration**: An *iteration* is a finite pre-defined *workflow* of *actions* initiated by a *request*.
- **request**: A *request* is described by a **parameter set** (the inputs) and it has a *response* (the outputs).
- **workflow**: A *workflow* is a *parameterised* network of procedural structures. e.g. A flowchart description of a process.
- **action**: An *action* uses an *expression* of *capabilities* to transform an input into an output.
- **expression**: An *expression* is a functional structure. e.g. A mathematical formula.
- **capability**: A *capability* is a functional unit. e.g. A mathematical operation.
- **response**: A *response* is the output of a *workflow*.
- **accomplished**: An *intentïon* infers the state when *accomplished*. e.g. An intention to walk is accomplished when walking.
- **parameterised**: A *workflow* is *parameterised* using a *heuristic* to select a *parameter set* from the *parameter search space*. 
- **parameter search space**: The *parameter search space* is every *parameter set* requires to access all the navigable paths through the network and the full range of inputs for the *actions*.
- **parameter set**: A *parameter set* is a set of inputs that can be used to execute an *iteration*.
- **heuristic**: A *heuristic* can apply the *intentïon* to select a *parameter set* from the *search space*.
- **enhancement**: An *enhancement* is the *heuristic* applied to previous request & responses and the *intentïon*.

## Getting Started

# Infrastructure setup

## Prerequisites

A user with full IAM access to create a role will be needed to execute the Terraform scripts which in turn to create
the infrastructure level resources such as:
* The IAM role for the deployment user

Software required:
* [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
* [jq](https://stedolan.github.io/jq/download/)
* [Terraform](https://www.terraform.io/downloads.html)
* Terragrunt
```shell
 % brew install terragrunt
```

## Steps

* Run:
```shell
 % export AWS_ACCOUNT_ID='541134664601'
 % export AWS_ACCESS_KEY_ID='...redacted...'
 % export AWS_SECRET_ACCESS_KEY='...redacted...'
 % aws sts get-caller-identity                                            
{
    "UserId": "AIDAI5QAEKWGGXBYLAZ5G",
    "Account": "541134664601",
    "Arn": "arn:aws:iam::541134664601:user/polycode-deploy"
}
 % ./aws-create-infrastructure-role.sh
```

Assume the role created by the script and use Terragrunt to create the infrastructure level resources by running:
```shell
 % source ./aws-reset-assumed-role.sh
{
    "UserId": "AIDAX37RDWOMUJDFBDE6Y",
    "Account": "541134664601",
    "Arn": "arn:aws:iam::541134664601:user/polycode-default-account"
}
 % source ./aws-assume-infrastructure-role.sh
 {
    "UserId": "AROA45MW5HDLQ2F53F3V4:WorkstationSession-for-antony",
    "Account": "541134664601",
    "Arn": "arn:aws:sts::541134664601:assumed-role/xn--intenton-z2a-web-infrastructure-role/WorkstationSession-for-antony"
}
 % terragrunt apply -auto-approve --terragrunt-non-interactive --terragrunt-working-dir ./infrastructure
```

Assume the role created by the script by running:
```shell
 % source ./aws-reset-assumed-role.sh
{
    "UserId": "AIDAX37RDWOMUJDFBDE6Y",
    "Account": "541134664601",
    "Arn": "arn:aws:iam::541134664601:user/polycode-default-account"
}
 % source ./aws-assume-deployment-role.sh
{
    "UserId": "AROA45MW5HDLRUMTIBS4I:WorkstationSession-for-antony",
    "Account": "887764105431",
    "Arn": "arn:aws:sts::887764105431:assumed-role/diyaccounting-co-uk-account-deployment-role/WorkstationSession-for-antony"
}
 %
```

## Infrastructure tear-down

Run the following in sequence:
```shell
 % source ./aws-assume-infrastructure-role.sh
 % terragrunt run-all destroy -auto-approve --terragrunt-non-interactive --terragrunt-working-dir ./infrastructure
 % ./aws-delete-infrastructure-role.sh
```

# CDK setup

Install the AWS CDK and ensure that version 2 is installed:
````bash
 % npm install -g aws-cdk
 % cdk --version
2.140.0 (build 46168aa)
````

## Preparing the AWS account for CDK deployment

Bootstrap the stack with a state bucket
```bash
 % source ./aws-reset-assumed-role.sh
{
    "UserId": "AIDAX37RDWOMUJDFBDE6Y",
    "Account": "541134664601",
    "Arn": "arn:aws:iam::541134664601:user/polycode-default-account"
}
 % source ./aws-assume-deployment-role.sh
{
    "UserId": "AROA45MW5HDLRUMTIBS4I:WorkstationSession-for-antony",
    "Account": "887764105431",
    "Arn": "arn:aws:sts::887764105431:assumed-role/diyaccounting-co-uk-account-deployment-role/WorkstationSession-for-antony"
}
 % TODO...
````

# Handy scripts

Cat the workflow and source files to datestamp the files in the export directory.
```shell
./export-source.sh
```

# Prompts

Website brief:
```shell
I want a single index.html file that is well-formed, declares
and adheres to all the latest accessibility guidelines and is
responsively rendered on all mainstream devices.

The page should render the word intentïon when the screen is
tapped (or mouse moved) for 3 seconds then fade out.

The word intentïon should be in dark grey (charcoal?) and
as wide as the horizontal viewport. The background should be
light grey, almost white with a hint of yellow (like fog
under bright sunlight).

The background (full screen, no text) should have the
attached images all fading in and out of transparency at
different rates.

Please show the HTML (all inline JS and CSS) the images
and any libraries you pull in would be links.
```

## CDK  Installation and initialisation of a project deployment directory

Create a deployment directory, initialise it with a CDK project then move the files to the root of the repository:
````bash
 % mkdir account
 % cd ./account
 % cdk init app --language java
Applying project template app for java
# Welcome to your CDK Java project!
...
 % rm -rf target README .gitignore
mv -v * ../.
cdk.json -> .././cdk.json
pom.xml -> .././pom.xml
src -> .././src
 % cd ..
 % rm -rf ./account
````

## The CDK README

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

Useful commands:

* `./mvnw package`     compile and run tests
* `cdk ls`          list all stacks in the app
* `cdk synth`       emits the synthesized CloudFormation template
* `cdk deploy`      deploy this stack to your default AWS account/region
* `cdk diff`        compare deployed stack with current state
* `cdk docs`        open CDK documentation

# Ownership

`xn--intenton-z2a.com` is a project by Polycode Limited which presents the intentïon home page: https://xn--intenton-z2a.com/

# License

Copyright (c) 2024 Polycode Limited

All rights reserved.

This software and associated documentation files (the "Software") are the property of Polycode Limited and are strictly confidential.
This Software is solely for use by individuals or entities that have been granted explicit permission by Polycode Limited.
This Software may not be copied, modified, distributed, sublicensed, or used in any way without the express written permission of Polycode Limited.

# Chats

intentïon: brand: https://chat.openai.com/share/(TODO: ChatGPT share link is broken)

# Thank you

Thank you for your interest in intentïon. Please be careful with our public brand.
