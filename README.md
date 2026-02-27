# intentïon

**intentïon: The feedback loop of innovation**

The public website for intentïon at [xn--intenton-z2a.com](https://xn--intenton-z2a.com/).

## What This Is

A minimalist single-page website presenting the intentïon brand. Dark text on an animated fog background, with community interaction via [Giscus](https://giscus.app/) (GitHub Discussions).

The site showcases autonomous code evolution experiments running on [repository0](https://github.com/xn-intenton-z2a/repository0), powered by [agentic-lib](https://github.com/xn-intenton-z2a/agentic-lib).

## Pronunciation

intentïon. /ɪnˈtɛnʃən/. The diaeresis is a style thing. Pronounce your intentïon as you please.

## Infrastructure

AWS CDK (Java) deploying:
- **CloudFront** + **S3** for static hosting
- **Route53** for DNS
- **CloudTrail** for telemetry
- **Lambda** functions for access log forwarding

Stacks: `NetworkStack` (DNS/SSL), `ApplicationStack` (S3/CloudFront), `TelemetryStack` (CloudTrail).

Environments: `dev` (auto-deploy on push), `live` (manual trigger).

## Development

```bash
npm install
./mvnw clean verify
```

See `_developers/` for scripts, brand assets, and setup docs.

## Social

- [LinkedIn](https://www.linkedin.com/company/intentïon)
- [Facebook](https://www.facebook.com/profile.php?id=61559328506140)
- [Twitter/X](https://twitter.com/intentiionai)
- [Instagram](https://www.instagram.com/intentiionaii)
- [LinkTree](https://linktr.ee/intentiion)

## License

AGPL-3.0 — see [LICENSE](LICENSE).

Copyright (c) 2024-2026 Polycode Limited.
