// @ts-check

const config = {
  title: '⚡ Thunder',
  tagline: 'CTAs, Nudges and Behaviour Tags platform',
  url: 'https://dream-horizon-org.github.io',
  baseUrl: '/raven/thunder/',
  favicon: 'img/logo.svg',
  organizationName: 'dream-horizon-org',
  projectName: 'raven',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  trailingSlash: false,

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          path: 'docs',
          routeBasePath: '/', // Docs as root
          sidebarPath: require.resolve('./sidebars.js'),
          editCurrentVersion: false,
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
        },
        blog: false, // Disable blog
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: '⚡ Thunder',
        logo: {
          alt: 'Thunder Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'docs',
            position: 'left',
            label: 'Docs',
          },
          {
            href: 'https://github.com/dream-horizon-org/thunder',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Documentation',
            items: [
              {
                label: 'Getting Started',
                to: '/raven/thunder/getting-started/overview',
              },
              {
                label: 'Architecture',
                to: '/raven/thunder/architecture/modules',
              },
              {
                label: 'API Reference',
                to: '/raven/thunder/api/overview',
              },
            ],
          },
          {
            title: 'Resources',
            items: [
              {
                label: 'Admin API',
                to: '/raven/thunder/admin/overview',
              },
              {
                label: 'Operations',
                to: '/raven/thunder/operations/docker',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/dream-horizon-org/thunder',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Thunder. Built with Docusaurus.`,
      },
      colorMode: {
        defaultMode: 'light',
        disableSwitch: false,
        respectPrefersColorScheme: true,
      },
      // Algolia search can be added later if needed
      // algolia: {
      //   appId: 'YOUR_APP_ID',
      //   apiKey: 'YOUR_SEARCH_API_KEY',
      //   indexName: 'YOUR_INDEX_NAME',
      // },
    }),
};

module.exports = config;



