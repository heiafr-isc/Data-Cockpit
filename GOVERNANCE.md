# Data-Cockpit Project Governance
<!-- Template from https://contribute.cncf.io/maintainers/github/templates/required/governance-maintainer/ -->


The Data-Cockpit project is dedicated to creating a useful, complete and free tool to conduct research experiments. 
This governance explains how the project is run.

- [Data-Cockpit Project Governance](#data-cockpit-project-governance)
  - [Values](#values)
  - [Community](#community)
    - [Community Members](#community-members)
    - [Contributors](#contributors)
    - [Reviewers](#reviewers)
    - [Maintainers](#maintainers)
      - [Electing a Maintainer](#electing-a-maintainer)
      - [Revoking a Maintainer](#revoking-a-maintainer)
    - [Core Maintainers](#core-maintainers)
  - [Maintainer council](#maintainer-council)
  - [Meetings](#meetings)
  - [Code of Conduct](#code-of-conduct)
  - [Security Response Team](#security-response-team)
  - [Voting](#voting)
  - [Modifying this Charter](#modifying-this-charter)

## Values

The Data-Cockpit and its leadership embrace the following values:

* Openness: Communication and decision-making happens in the open and is discoverable for future
 reference. As much as possible, all discussions and work take place in public
 forums and open repositories.

* Fairness: All stakeholders have the opportunity to provide feedback and submit
 contributions, which will be considered on their merits.

* Community over Product or Company: Sustaining and growing our community takes
 priority over shipping code or sponsors' organizational goals. Each
 contributor participates in the project as an individual.

* Inclusivity: We innovate through different perspectives and skill sets, which
 can only be accomplished in a welcoming and respectful environment.

* Participation: Responsibilities within the project are earned through
 participation, and there is a clear path up the contributor ladder into leadership
 positions.

## Community

The Data-Cockpit project aims to be driven by its community. The below section outlines 
the different roles of community members within the project, along with the 
responsibilities and privileges that come with them.

### Community Members

Community Members are all users who interact with the project. This could be through Discord, GitHub discussions, joining public project meetings, mailing lists, etc.

Responsibilities:
 - Respect the [Code Of Conduct](CODE_OF_CONDUCT.md).

### Contributors

Contributors are [Community Members](#community-members) who [contribute](https://opensource.guide/how-to-contribute/#what-it-means-to-contribute) directly to the project and add value to it. This can be
through code, documentation, taking part in bug scrubs, opening issues, proposing a pull request, etc.

Defined by:
 - Having valid contributions (as per [GitHub definition](https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-github-profile/managing-contribution-settings-on-your-profile/viewing-contributions-on-your-profile#what-counts-as-a-contribution)) under the
[Data-Cockpit](https://github.com/heiafr-isc/Data-Cockpit) GitHub repository.
 - Any support, feedback, or engagement that helps improve the project.

Responsibilities:
 - Respect the [Code Of Conduct](CODE_OF_CONDUCT.md).
 - Respect the [Contribution Guidelines](./CONTRIBUTING.md).
 - [Sign off](https://git-scm.com/docs/git-commit#Documentation/git-commit.txt---signoff) Git Commits to certify they adhere to the [Developer Certificate of Origin (DCO)](https://developercertificate.org/).

### Reviewers

Reviewers are [Contributors](#contributors) who have technical experience in an area of the project, and are willing to help in reviewing pull requests. They are added or removed at the sole discretion of repository administrators.

Defined by:
 - The CODEOWNERS file.

Responsibilities:
 - Review pull requests.
 - Follow the [Maintainers](#maintainers) guidelines.

### Maintainers

Maintainers are [Contributors](#contributors) who have shown significant and sustained contribution. They are highly experienced reviewers and contributors to a specific area of the project.

Requirements:
 - Active contribution and participation in one or more areas of the project.
 - Domain expertise and a good understanding of the code-base of those areas.
 - Comprehensive understanding of project governance.
 - Known to uphold the projects best interests.

Measurement of these criteria are subject to the determination of the existing maintainers as attested by the sponsoring maintainer (the person working with the interested contributor to show them the ropes).

Defined by:
 - The MAINTAINERS.md file `Maintainers list` entry

Responsibilities:
 - Be active and proactive in communications, lead community calls, and help other community members.
 - Monitor [the Discord server](https://discord.gg/GYHqyQYg7V). Delayed responses are acceptable.
 - Triage GitHub issues and review pull requests (PRs).
 - Make sure that PRs are moving forward at the right pace, or closing them.
 - Participate when called upon in security releases. Although this should be a rare occurrence, if a serious vulnerability is found it may take up to several full days of work.
 - Mentoring, encouraging, and sponsoring new reviewers and maintainers.

#### Electing a Maintainer

Once a contributor has demonstrated a sustained commitment to the project, they may be nominated for Maintainer status. A simple majority vote of existing Maintainers approves
the application. Maintainers nominations will be evaluated without prejudice to employer
or demographics.

Maintainers who are selected will be granted the necessary GitHub rights,
and will be promoted on [the Discord server](https://discord.gg/GYHqyQYg7V).

#### Revoking a Maintainer

Maintainers may resign at any time if they feel that they will not be able to
continue fulfilling their project duties.

Maintainers may also be removed after being inactive, failure to fulfil their 
Maintainer responsibilities, violating the Code of Conduct, or other reasons.
Inactivity is defined as a period of very low or no activity in the project 
for a year or more, with no definite schedule to return to full Maintainer 
activity.

A Maintainer may be removed at any time by a 2/3 vote of the remaining maintainers.

Depending on the reason for removal, a Maintainer may be converted to Emeritus
status. Emeritus Maintainers will still be consulted on some project matters,
and can be rapidly returned to Maintainer status if their availability changes.

### Core Maintainers 

Core Maintainers are the original [Maintainers](#maintainers) of the Data-Cockpit project.
The Core Maintainers form a team that drives the direction, values, and governance of the 
overall project. They also serve as an escalation point for the overall project, and 
anything not easily managed by the maintainers.

Defined by:
 - The MAINTAINERS.md file `Core maintainers list` entry

Responsibilities and privileges:
 - Overseeing the overall project health and growth.
 - Speaking on behalf of the project.
 - Maintaining the brand, mission, vision, values, and scope of the project.
 - Defining general guidelines for the project.
 - Administering the [Data-Cockpit](https://github.com/heiafr-isc/Data-Cockpit) GitHub repository.
 - Administering any assets or services owned or assigned to project.
 - Handle licence and copyright issues.
 - Look out for issues or conflicts in any area of the project.
 - Serve as the last escalation point for an issue that can't be solved by other community roles.
 - Ability to create committees and delegate powers to them.

Any decisions made by the Core Maintainers could be appealed by any maintainer to the
Maintainer council.

## Maintainer council

The Maintainer council is composed of all current Maintainers. They are responsible for the overall governance of the project and making decisions on behalf of the community.

## Meetings

Time zones permitting, Maintainers are expected to participate in the public
developer meetings. The schedule for these meetings will be communicated via
[the Discord server](https://discord.gg/GYHqyQYg7V).

Maintainers will also have closed meetings in order to discuss security reports
or Code of Conduct violations. Such meetings should be scheduled by any
Maintainer on receipt of a security issue or CoC report. All current Maintainers
must be invited to such closed meetings, except for any Maintainer who is
accused of a CoC violation.

## Code of Conduct

[Code of Conduct](./code-of-conduct.md)
violations by community members will be discussed and resolved
on [the Discord server](https://discord.gg/GYHqyQYg7V) on private channels. If a 
Maintainer is directly involved in the report, the Maintainers will instead designate
two Maintainers to work with the CNCF Code of Conduct Committee in resolving it.

## Security Response Team

The Maintainers will appoint a Security Response Team to handle security reports.
This committee may simply consist of the Maintainer Council themselves. If this
responsibility is delegated, the Maintainers will appoint a team of at least two
contributors to handle it. The Maintainers will review who is assigned to this
at least once a year.

The Security Response Team is responsible for handling all reports of security
holes and breaches according to the [security policy](./security.md).

## Voting

While most business in Data-Cockpit is conducted by "[lazy consensus](https://community.apache.org/committers/lazyConsensus.html)", 
periodically the Maintainers may need to vote on specific actions or changes.
A vote can be taken on [the Discord server](https://discord.gg/GYHqyQYg7V) or on
the private Maintainer mailing list for security or conduct matters. 
Votes may also be taken at the developer meetings. Any Maintainer may
demand a vote be taken.

Most votes require a simple majority of all Maintainers to succeed, except where
otherwise noted. Two-thirds majority votes mean at least two-thirds of all 
existing maintainers.

## Modifying this Charter

Changes to this Governance and its supporting documents may be approved by 
a 2/3 vote of the Maintainers.
